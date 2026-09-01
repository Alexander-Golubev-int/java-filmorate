package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.dto.UpdateUserRequestDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.UserDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public final class RowMapperUser implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getObject("birthday", LocalDate.class));
        return user;
    }

    public UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setLogin(user.getLogin());
        dto.setBirthday(user.getBirthday());
        return dto;
    }

    public User updateUserFields(User user, UpdateUserRequestDto request) {
        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }
        if (request.hasLogin()) {
            user.setLogin(request.getLogin());
        }
        if (request.hasName()) {
            user.setName(request.getName());
        }
        if ((request.hasBirthday()))
            user.setBirthday(user.getBirthday());
        return user;
    }
}
