package ru.yandex.practicum.filmorate.storage.dal.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FilmDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Integer likes;
    private MpaDto mpa = new MpaDto();
    private List<GenreDto> genres;
}