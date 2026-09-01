package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

public enum Genre {
    COMEDY(1, "Комедия"),
    DRAMA(2, "Драма"),
    CARTOON(3, "Мультфильм"),
    THRILLER(4, "Триллер"),
    DOCUMENTARY(5, "Документальный"),
    ACTION(6, "Боевик");

    private final int id;
    @Getter
    private final String name;

    Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonCreator
    public static Genre fromId(int id) {
        return Arrays.stream(values())
                .filter(genre -> genre.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный id жанра: " + id));
    }

    @JsonValue
    public int getId() {
        return id;
    }

}