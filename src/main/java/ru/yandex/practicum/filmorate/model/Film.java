package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.Create;
import ru.yandex.practicum.filmorate.validator.Update;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    @NotNull(groups = Update.class, message = "Необходимо указать id фильма")
    private Long id;
    @NotNull(groups = Create.class, message = "Название должно быть указано")
    @NotBlank(groups = Create.class, message = "Название фильма не может быть пустым ")
    private String name;
    @NotNull(groups = Create.class, message = "Необходимо указать описание")
    @Size(groups = {Create.class, Update.class}, max = 200, message = "Максимальная длинна описания 200 символов")
    @NotBlank(groups = Create.class, message = "Необходимо указать описание фильма")
    private String description;
    @NotNull(groups = Create.class, message = "Необходимо указать дату создания фильма yyyy-mm-dd")
    private LocalDate releaseDate;
    @NotNull(groups = Create.class, message = "Продолжительность должна быть указан")
    @Min(groups = {Create.class, Deprecated.class}, value = 1, message = "Продолжительность должна быть положительным числом")
    private Integer duration;
}
