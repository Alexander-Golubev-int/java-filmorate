package ru.yandex.practicum.filmorate.storage.dal.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.DateNotBefore;
import ru.yandex.practicum.filmorate.validator.Create;
import ru.yandex.practicum.filmorate.validator.Update;

import java.time.LocalDate;
import java.util.List;

@Data
public class NewFilmRequest {
    @NotBlank(groups = Create.class, message = "Название фильма не может быть пустым")
    private String name;
    @Size(groups = Create.class, max = 200, message = "Максимальная длинна описания 200 символов")
    @NotBlank(groups = Create.class, message = "Необходимо указать описание фильма")
    private String description;
    @NotNull(groups = Create.class, message = "Необходимо указать дату создания фильма yyyy-mm-dd")
    @DateNotBefore(groups = {Create.class, Update.class})
    private LocalDate releaseDate;
    @NotNull(groups = Create.class, message = "Продолжительность должна быть указана")
    @Min(groups = Create.class, value = 1, message = "Продолжительность должна быть положительным числом")
    private Integer duration;
    @NotNull(groups = Create.class, message = "Должен быть указан рейтинг MPA")
    private AgeRatingDto mpa;
    private List<GenreDto> genres;
}