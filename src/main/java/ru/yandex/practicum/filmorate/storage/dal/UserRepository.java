package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
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
    private static final String FIND_ALL_QUERY = "SELECT * FROM \"Users\"";
    private static final String FIND_ALL_FRIENDS_USERS = """
            SELECT u.*
            FROM "Users" u
            JOIN "Friendship" f ON (
                (f.from_user_id = ? AND f.to_user_id = u.user_id)
                OR
                (f.to_user_id = ? AND f.from_user_id = u.user_id)
            )
            WHERE f.friendship_status_id = 2
              AND u.user_id != ?;
    """;
    private static final String FIND_FRIENDSHIP = "SELECT COUNT(*) FROM \"Friendship\" WHERE from_user_id " +
            "= ? AND to_user_id = ? AND friendship_status_id = 2";
    private static final String FIND_SUBMITTED_APPLICATION = "SELECT COUNT(*) FROM \"Friendship\" WHERE from_user_id = ? AND to_user_id = ? AND friendship_status_id = 1";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM \"Users\" WHERE user_id = ?";
    private static final String FIND_BY_ID_INCOMING_REQUEST = "SELECT COUNT(*) FROM \"IncomingRequestToFriends\" WHERE user_id = ? AND from_user_id = ?";

    private static final String INSERT_QUERY_PENDING = "INSERT INTO \"Friendship\"(from_user_id, to_user_id, friendship_status_id)" +
            "VALUES (?, ?, 1)";
    private static final String INSERT_QUERY_INCOMING_REQUEST_TO_FRIENDS = "INSERT INTO \"IncomingRequestToFriends\"(user_id, from_user_id)" +
            "VALUES (?, ?)";
    private static final String INSERT_QUERY_ACCEPTED = "INSERT INTO \"Friendship\"(from_user_id, to_user_id, " +
            "friendship_status_id)" +
            "VALUES (?, ?, 2)";

    private static final String UPDATE_QUERY_FRIENDSHIP = "UPDATE \"Friendship\" SET friendship_status_id = 2 WHERE " +
            "from_user_id = ? AND to_user_id = ?;";

    private static final String DELETE_QUERY_INCOMING_REQUEST_TO_FRIENDS = "DELETE FROM \"IncomingRequestToFriends\" " +
            "WHERE user_id = ? AND from_user_id = ?;";
    private static final String DELETE_QUERY_FRIENDSHIPS = "DELETE FROM \"Friendship\" " +
            "WHERE from_user_id = ? AND to_user_id = ?;";

    public List<User> findAll() {
        return jdbc.query(FIND_ALL_QUERY, new RowMapperUser());
    }

    public List<User> findAllFriends(long userId) {
        return jdbc.query(FIND_ALL_FRIENDS_USERS, new RowMapperUser(), userId, userId, userId);
    }

    public Optional<User> findById(long userId) {
        List<User> list = jdbc.query(FIND_BY_ID_QUERY, new RowMapperUser(), userId);
        return list.stream().findFirst();
    }

    public boolean areFriends(Long userId, Long friendId) {
        return jdbc.queryForObject(FIND_FRIENDSHIP, Integer.class, userId, friendId) > 0;
    }

    public boolean isAnIncomingRequest(Long id, Long fromUserId) {
        return jdbc.queryForObject(FIND_BY_ID_INCOMING_REQUEST, Integer.class, id, fromUserId) > 0;
    }

    public boolean isFriendRequestPending(Long id, Long friendId) {
        return jdbc.queryForObject(FIND_SUBMITTED_APPLICATION, Integer.class, id, friendId) > 0;
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

    public void deleteFriendships(Long userId, Long friendId) {
        jdbc.update(DELETE_QUERY_FRIENDSHIPS, userId, friendId);
    }

    public void addNewFriend(Long user_id, Long friendId) {
        try {
            if (insertWithoutKeys(INSERT_QUERY_PENDING, user_id, friendId)) {
                log.info("Пользователь {} отправил заявку на добавление в друзья пользователю {}", user_id, friendId);
            }

        } catch (DuplicateKeyException e) {
            log.warn("Не удалось сохранить запрос на отправку дружбы от: {} к {}", user_id, friendId);
            throw new ValidationException("Не удалось сохранить запрос на отправку дружбы от: " +
                    "from_user_id=" + user_id + ", to_user_id=" + friendId);
        }
    }

    public void addIncomingRequestToFriends(Long friendId, Long user_id) {
        try {
            if (insertWithoutKeys(INSERT_QUERY_INCOMING_REQUEST_TO_FRIENDS, friendId, user_id)) {
                log.info("Создана входящая заявка на добавление друга пользователю {} от {}", friendId,
                        user_id);
            }
        } catch (DuplicateKeyException e) {
            log.warn("Не удалось сохранить запрос на создание заявки в друзья между пользователем {} и {}",
                    friendId, user_id);
            throw new ValidationException(String.format("Не удалось сохранить запрос на создание заявки в друзья между пользователем %s и %s",
                    friendId, user_id));
        }
    }

    public boolean insertWithoutKeys(String sql, Long userId, Long friendId) {
        try {
            int rowsAffected = jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, userId);
                ps.setLong(2, friendId);
                return ps;
            });
            return rowsAffected > 0;
        } catch (DuplicateKeyException e) {
            throw new ValidationException("Ошибка в методе insertWithoutKeys при вставке данных");
        }
    }


    protected long insert(String sql, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;}, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            return id;
        } else {
            return -1;
        }
    }

}
