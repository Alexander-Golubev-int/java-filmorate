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
        return new AgeRatingDto(
                (long) ageRating.getId(),
                ageRating.name()
        );
    }
}