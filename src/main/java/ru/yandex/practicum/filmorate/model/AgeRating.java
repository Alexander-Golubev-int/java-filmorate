package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AgeRating {
    G(1, "У фильма нет возрастных ограничений"),
    PG(2, "Детям рекомендуется смотреть фильм с родителями"),
    PG_13(3, "Детям до 13 лет просмотр не желателен"),
    R(4, "Лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
    NC_17(5, "Лицам до 18 лет просмотр запрещён");

    private final int id;
    private final String description;

    AgeRating(int id, String description) {
        this.id = id;
        this.description = description;
    }

    @JsonCreator
    public static AgeRating fromId(int id) {
        return Arrays.stream(values())
                .filter(rating -> rating.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный id: " + id));
    }

    @JsonValue
    public int getId() {
        return id;
    }
}
