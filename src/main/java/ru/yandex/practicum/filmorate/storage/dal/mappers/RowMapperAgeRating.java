package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.storage.dal.dto.AgeRatingDto;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RowMapperAgeRating implements RowMapper<AgeRatingDto> {
    @Nullable
    @Override
    public AgeRatingDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        AgeRatingDto ageRating = new AgeRatingDto();
        ageRating.setId(rs.getLong("id"));
        ageRating.setName(rs.getString("age_rating"));
        return ageRating;
    }
}
