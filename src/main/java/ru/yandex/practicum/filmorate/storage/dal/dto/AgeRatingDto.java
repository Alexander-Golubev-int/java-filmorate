package ru.yandex.practicum.filmorate.storage.dal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.AgeRating;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgeRatingDto {
    private Long id;
    private String name;

    public static AgeRatingDto fromAgeRating(AgeRating ageRating) {
        String name = switch (ageRating) {
            case G -> "G";
            case PG -> "PG";
            case PG_13 -> "PG-13";
            case R -> "R";
            case NC_17 -> "NC-17";
        };
        return new AgeRatingDto((long) ageRating.getId(), name);
    }
}