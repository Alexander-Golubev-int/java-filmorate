package ru.yandex.practicum.filmorate.storage.dal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.Create;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    @Email(groups = {Create.class}, regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Неправильно указан email")
    @NotNull(groups = Create.class, message = "Email не должен быть пустым")
    private String email;
    @Pattern(groups = {Create.class}, regexp = "^[a-zA-Zа-яА-ЯёЁ0-9_]+$", message = "Логин должен содержать только " +
            "буквы и без пробелов")
    @NotNull(groups = {Create.class}, message = "Логин не должен быть пустым")
    private String login;
    private String name;
    @NotNull(groups = {Create.class}, message = "Дата дня рождения не может быть null")
    @PastOrPresent(groups = {Create.class},
            message = "Дата дня рождения не может быть в будущем")
    private LocalDate birthday;
}
