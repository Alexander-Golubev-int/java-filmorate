package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Genre {
    COMEDY(1),
    DRAMA(2),
    CARTOON(3),
    THRILLER(4),
    DOCUMENTARY(5),
    ACTION(6);

    private final int id;

    Genre(int id) {
        this.id = id;
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