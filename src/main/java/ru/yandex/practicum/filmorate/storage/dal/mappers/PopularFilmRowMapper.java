package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.storage.dal.dto.FilmDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class PopularFilmRowMapper implements RowMapper<FilmDto> {
    @Override
    public FilmDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmDto film = new FilmDto();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getObject("release_date", LocalDate.class));
        film.setDuration(rs.getInt("duration"));
        film.setLikes(rs.getInt("likes"));
        return film;
    }
}
