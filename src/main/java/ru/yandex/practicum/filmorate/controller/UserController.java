package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.Create;
import ru.yandex.practicum.filmorate.validator.Update;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Validated(Create.class) @RequestBody User user) {
        if (users.values().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            log.warn("Попытка добавить уже существующего пользователя {}", user);
            throw new DuplicatedDataException("Такой пользователь уже существует");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь {} успешно создан и добавлен в список", user);
        return user;

    }

    @PutMapping
    public User updateUser(@Validated(Update.class) @RequestBody User user) {
        if (user == null) {
            log.warn("Отправлен не проинициализированный пользователь");
            throw new NotFoundDataException("Необходимо отправить корректный json формат пользователя");
        }

        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с ID = {} уже существует", user.getId());
            throw new NotFoundDataException("ID с таким пользователем не существует");
        }

        if (users.containsValue(user)) {
            log.warn("Попытка изменить пользователя, но без измененных полей {}", user);
            throw new DuplicatedDataException("Такой пользователь уже есть");
        }

        User newUser = users.get(user.getId());

        if (user.getEmail() != null) {
            log.info("Старый email = {} успешно сменен на новый {}", newUser.getEmail(), user.getEmail());
            newUser.setEmail(user.getEmail());
        }

        if (user.getLogin() != null) {
            log.info("Старый login = {} успешно сменен на новый {}", newUser.getLogin(), user.getLogin());
            newUser.setLogin(user.getLogin());
        }

        if (user.getName() != null) {
            log.info("Старое имя = {} успешно сменено на новое {}", newUser.getName(), user.getName());
            newUser.setName(user.getName());
        }

        if (user.getBirthday() != null) {
            log.info("Старая дата рождения = {} успешно сменена на новое {}", newUser.getBirthday(), user.getBirthday());
            newUser.setBirthday(user.getBirthday());
        }
        log.info("Пользователь {} успешно изменил данные", newUser);
        users.replace(newUser.getId(), newUser);
        return newUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
