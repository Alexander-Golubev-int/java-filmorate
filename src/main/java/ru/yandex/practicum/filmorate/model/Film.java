package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.storage.dal.dto.GenreDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Long ageRating;
    private String mpa;
    private List<GenreDto> genres = new ArrayList<>();
    private Integer likes;
}

