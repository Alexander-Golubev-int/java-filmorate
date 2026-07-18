package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.Create;
import ru.yandex.practicum.filmorate.validator.Update;

import java.time.LocalDate;

@Data
public class User {
    @NotNull(groups = Update.class, message = "Необходимо указать id пользователя")
    private Long id;
    @Email(groups = {Create.class, Update.class}, regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Неправильно указан email")
    @NotNull(groups = Create.class, message = "Необходимо указать email")
    @NotBlank(groups = Create.class, message = "Email не должен быть пустым")
    private String email;
    @NotNull(groups = Create.class, message = "Необходимо указать логин")
    @NotBlank(groups = Create.class, message = "Логин не должен быть пустым")
    private String login;
    private String name;
    @NotNull(message = "Дата дня рождения не может быть null")
    @PastOrPresent(groups = {Create.class, Update.class},
            message = "Дата дня рождения не может быть в будущем")
    private LocalDate birthday;
}
