package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> getUsers();

    User createUser(@Valid @RequestBody User user);

    User updateUser(@Valid @RequestBody User user);

}
