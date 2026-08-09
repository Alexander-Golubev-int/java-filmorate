package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;
import ru.yandex.practicum.filmorate.validator.Create;
import ru.yandex.practicum.filmorate.validator.Update;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class User {
    @NotNull(groups = Update.class, message = "Необходимо указать id пользователя")
    private Long id;
    @Email(groups = {Create.class, Update.class}, regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Неправильно указан email")
    @NotNull(groups = Create.class, message = "Email не должен быть пустым")
    private String email;
    @Pattern(groups = {Create.class, Update.class}, regexp = "^[a-zA-Zа-яА-ЯёЁ0-9_]+$", message = "Логин должен содержать только " +
            "буквы и без" +
            " " +
            "пробелов")
    @NotNull(groups = Create.class, message = "Логин не должен быть пустым")
    private String login;
    private String name;
    @NotNull(groups = {Create.class, Update.class}, message = "Дата дня рождения не может быть null")
    @PastOrPresent(groups = {Create.class, Update.class},
            message = "Дата дня рождения не может быть в будущем")
    private LocalDate birthday;
    private Map<Long, Friendship> friends = new HashMap<>();
    @ToString.Exclude
    private Map<Long, Film> favoriteFilms = new HashMap<>();
    private final IncomingRequestsFriends requestsFriendsSetList = new IncomingRequestsFriends();
}
