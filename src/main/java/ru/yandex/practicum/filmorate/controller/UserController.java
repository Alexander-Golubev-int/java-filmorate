package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.service.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.service.UserService;
import ru.yandex.practicum.filmorate.validator.Create;
import ru.yandex.practicum.filmorate.validator.Update;

import java.util.Collection;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final InMemoryUserStorage inMemoryUserStorage;
    private final UserService userService;

    @GetMapping
    public Collection<User> getUsers() {
        return inMemoryUserStorage.getUsers();
    }

    @GetMapping(path = "/{id}/friends")
    public Collection<User> getUsersFriends(@PathVariable @Positive(message = "ID должен быть > 0") Long id) {
        return userService.getFriendsUser(id);
    }

    @GetMapping(path = "/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriends(@PathVariable @Positive(message = "ID должен быть > 0") Long id,
                                             @PathVariable @Positive(message = "ID друга должен быть > 0") Long otherId) {
        return userService.getMutualFriends(id, otherId);
    }

    @PostMapping
    public User createUser(@Validated(Create.class) @RequestBody User user) {
        return inMemoryUserStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@Validated(Update.class) @RequestBody User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    @PutMapping(path = "/{id}/friends/{friendId}")
    public Map<String, String> updateUser(@PathVariable @Positive(message = "ID должен быть > 0") Long id,
                                          @PathVariable @Positive(message = "ID друга должен быть > 0") Long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping(path = "/{id}/friends/{friendId}")
    public Map<String, String> deleteUser(@PathVariable @Positive(message = "ID должен быть > 0") Long id,
                                          @PathVariable @Positive(message = "ID друга должен быть > 0") Long friendId) {
        return userService.deleteFriend(id, friendId);
    }

}
