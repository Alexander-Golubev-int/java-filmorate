package ru.yandex.practicum.filmorate.storage.dal.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.DateNotBefore;
import ru.yandex.practicum.filmorate.validator.Update;

import java.time.LocalDate;

@Data
public class UpdateFilmRequestDto {
    private final Long id;
    @NotBlank(groups = Update.class, message = "Название фильма не может быть пустым")
    private String name;
    @Size(groups = Update.class, max = 200, message = "Максимальная длинна описания 200 символов")
    @NotBlank(groups = Update.class, message = "Необходимо указать описание фильма")
    private String description;
    @NotNull(groups = Update.class, message = "Необходимо указать дату создания фильма yyyy-mm-dd")
    @DateNotBefore(groups = Update.class)
    private LocalDate releaseDate;
    @NotNull(groups = Update.class, message = "Продолжительность должна быть указана")
    @Min(groups = {Update.class}, value = 1, message = "Продолжительность должна быть положительным числом")
    private Integer duration;
    @NotNull(groups = Update.class, message = "Должен быть указан рейтинг фильма от 1 - 5")
    private AgeRatingDto mpa;

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null;
    }

    public boolean hasAgeRating() {
        return mpa != null;
    }
}

