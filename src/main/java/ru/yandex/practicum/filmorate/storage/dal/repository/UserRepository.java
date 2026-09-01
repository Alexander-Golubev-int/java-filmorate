package ru.yandex.practicum.filmorate.storage.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.storage.dal.dto.UpdateUserRequestDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.dal.mappers.RowMapperUser;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbc;
    private final RowMapperUser rowMapperUser;
    private static final String FIND_ALL_QUERY = "SELECT * FROM \"Users\"";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM \"Users\" WHERE user_id = ?";
    private static final String FIND_BY_ID_INCOMING_REQUEST = "SELECT COUNT(*) FROM \"IncomingRequestToFriends\" " +
            "WHERE user_id = ? AND from_user_id = ?";
    private static final String FIND_COMMON_FRIENDS = "SELECT DISTINCT (u.*) FROM \"Users\" u JOIN \"Friendship\" f1 ON (" +
            "(f1.from_user_id = ? AND f1.to_user_id = u.user_id) OR (f1.to_user_id = ? AND f1.from_user_id = u.user_id))" +
            " JOIN \"Friendship\" f2 ON ((f2.from_user_id = ? AND f2.to_user_id = u.user_id) OR (f2.to_user_id = ? AND " +
            "f2.from_user_id = u.user_id)) WHERE f1.friendship_status_id = 2 AND f2.friendship_status_id = 2 AND " +
            "u.user_id NOT IN (?, ?)";
    private static final String INSERT_QUERY_ACCEPTED = "INSERT INTO \"Friendship\"(from_user_id, to_user_id, " +
            "friendship_status_id) VALUES (?, ?, 2)";
    private static final String INSERT_NEW_USER = "INSERT INTO \"Users\"(email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY_FRIENDSHIP = "UPDATE \"Friendship\" SET friendship_status_id = 2 " +
            "WHERE from_user_id = ? AND to_user_id = ?;";
    private static final String UPDATE_USER = "UPDATE \"Users\" SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String DELETE_QUERY_INCOMING_REQUEST_TO_FRIENDS = "DELETE FROM \"IncomingRequestToFriends\" " +
            "WHERE user_id = ? AND from_user_id = ?;";
    private static final String INSERT_CONFIRMED_FRIENDSHIP = "INSERT INTO \"Friendship\"(from_user_id, to_user_id, " +
            "friendship_status_id) VALUES (?, ?, 2)";
    private static final String FIND_ALL_FRIENDS = "SELECT u.* FROM \"Users\" u JOIN \"Friendship\" f ON f.to_user_id = " +
            "u.user_id WHERE f.from_user_id = ? AND f.friendship_status_id = 2";
    private static final String ARE_FRIENDS = "SELECT COUNT(*) FROM \"Friendship\" WHERE from_user_id = ? AND " +
            "to_user_id = ? AND friendship_status_id = 2";
    private static final String DELETE_FRIENDSHIP = "DELETE FROM \"Friendship\" WHERE from_user_id = ? AND to_user_id = ?";

    public void addConfirmedFriend(Long userId, Long friendId) {
        jdbc.update(INSERT_CONFIRMED_FRIENDSHIP, userId, friendId);
    }

    public List<UserDto> findAllFriends(long userId) {
        return jdbc.query(FIND_ALL_FRIENDS, new RowMapperUser(), userId).stream()
                .map(rowMapperUser::mapToUserDto)
                .toList();
    }

    public boolean areFriends(Long userId, Long friendId) {
        Integer count = jdbc.queryForObject(ARE_FRIENDS, Integer.class, userId, friendId);
        return count != null && count > 0;
    }

    public void deleteFriendships(Long userId, Long friendId) {
        jdbc.update(DELETE_FRIENDSHIP, userId, friendId);
    }

    public List<UserDto> findAll() {
        return jdbc.query(FIND_ALL_QUERY, new RowMapperUser()).stream()
                .map(rowMapperUser::mapToUserDto)
                .toList();
    }

    public Optional<UserDto> findById(long userId) {
        List<UserDto> list = jdbc.query(FIND_BY_ID_QUERY, new RowMapperUser(), userId).stream()
                .map(rowMapperUser::mapToUserDto)
                .toList();
        return list.stream().findFirst();
    }

    public UserDto addNewUser(NewUserRequest user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        INSERT_NEW_USER,
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getLogin());
                ps.setString(3, user.getName());
                ps.setObject(4, user.getBirthday());
                return ps;
            }, keyHolder);

        } catch (DuplicateKeyException e) {
                throw new ValidationException("Пользователь с email: " + user.getEmail() + " уже существует");
        }
        Long userId = keyHolder.getKeyAs(Long.class);
        User userFromBd = jdbc.queryForObject(FIND_BY_ID_QUERY, rowMapperUser, userId);
        return rowMapperUser.mapToUserDto(userFromBd);
    }

    public UserDto updateUser(UpdateUserRequestDto user) {
        User userFromBd = jdbc.queryForObject(FIND_BY_ID_QUERY, rowMapperUser, user.getId());
        userFromBd = rowMapperUser.updateUserFields(userFromBd, user);
        jdbc.update(UPDATE_USER, userFromBd.getEmail(), userFromBd.getLogin(), user.getName(),
                user.getBirthday(), userFromBd.getId());
        userFromBd = jdbc.queryForObject(FIND_BY_ID_QUERY, rowMapperUser, user.getId());

        return rowMapperUser.mapToUserDto(userFromBd);
    }

    public boolean isAnIncomingRequest(Long id, Long fromUserId) {
        return jdbc.queryForObject(FIND_BY_ID_INCOMING_REQUEST, Integer.class, id, fromUserId) > 0;
    }

    public void confirmFriendship(Long friendId, Long userId) {
        jdbc.update(UPDATE_QUERY_FRIENDSHIP, friendId, userId);
    }

    public void addNewFriendAfterConfirmFriendship(Long userId, Long friendId) {
        jdbc.update(INSERT_QUERY_ACCEPTED, userId, friendId);
    }

    public void deleteIncomingRequestToFriends(Long userId, Long friendId) {
        jdbc.update(DELETE_QUERY_INCOMING_REQUEST_TO_FRIENDS, userId, friendId);
    }

    public List<UserDto> getCommonFriends(Long userId1, Long userId2) {
        return jdbc.query(FIND_COMMON_FRIENDS, new RowMapperUser(), userId1, userId1,
                userId2, userId2,
                userId1, userId2).stream()
                .map(rowMapperUser::mapToUserDto)
                .toList();
    }
}
