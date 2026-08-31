package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.dal.dto.GenreDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RowMapperGenreDto implements RowMapper<GenreDto> {

    @Override
    public GenreDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        GenreDto genreDto = new GenreDto();
        genreDto.setId(rs.getLong("id"));
        genreDto.setName(rs.getString("genre"));
        return genreDto;
    }
}