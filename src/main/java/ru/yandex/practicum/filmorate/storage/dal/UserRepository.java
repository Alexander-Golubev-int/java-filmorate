package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.mappers.RowMapperUser;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbc;
    private final RowMapperUser rmu;
    private static final String FIND_ALL_QUERY = "SELECT * FROM \"Users\"";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM \"Users\" WHERE email = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM \"Users\" WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO \"Users\"(username, email, password, registration_date)" +
            "VALUES (?, ?, ?, ?) returning id";
    private static final String UPDATE_QUERY = "UPDATE \"Users\" SET username = ?, email = ?, password = ? WHERE user_id = ?";


    public Optional<User> findById(long userId) {
        List<User> list = jdbc.query(FIND_BY_ID_QUERY, new RowMapperUser(), userId);
        return list.stream().findFirst();
    }

}
