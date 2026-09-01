package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import ru.yandex.practicum.filmorate.storage.dal.dto.AgeRatingDto;

import java.util.Arrays;

@Getter
public enum AgeRating {
    G(1),
    PG(2),
    PG_13(3),
    R(4),
    NC_17(5);

    private final int id;

    AgeRating(int id) {
        this.id = id;
    }

    @JsonCreator
    public static AgeRating fromId(int id) {
        return Arrays.stream(values())
                .filter(rating -> rating.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный id: " + id));
    }

    public static AgeRating fromMpa(AgeRatingDto ageRatingDto) {
        return Arrays.stream(values())
                .filter(rating -> rating.id == ageRatingDto.getId())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный id: " + ageRatingDto.getId()));
    }


    @JsonValue
    public int getId() {
        return id;
    }
}
