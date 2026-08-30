package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.storage.dal.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.storage.dal.dto.UpdateUserRequestDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.service.UserService;
import ru.yandex.practicum.filmorate.validator.Create;
import ru.yandex.practicum.filmorate.validator.Update;

import java.util.Collection;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping(path = "/{id}/friends")
    public Collection<UserDto> getUsersFriends(@PathVariable @Positive(message = "ID должен быть > 0") Long id) {
        return userService.getFriendsUser(id);
    }

    @GetMapping(path = "/{id}/friends/common/{otherId}")
    public Collection<UserDto> getMutualFriends(@PathVariable @Positive(message = "ID должен быть > 0") Long id,
                                                @PathVariable @Positive(message = "ID друга должен быть > 0") Long otherId) {
        return userService.getMutualFriends(id, otherId);
    }

    @PostMapping
    public UserDto createUser(@Validated(Create.class) @RequestBody NewUserRequest user) {
        return userService.createUser(user);
    }

    @PutMapping
    public UserDto updateUser(@Validated(Update.class) @RequestBody UpdateUserRequestDto user) {
        return userService.updateUser(user);
    }

    @PutMapping(path = "/{id}/friends/{friendId}")
    public Map<String, String> updateUser(@PathVariable @Positive(message = "ID должен быть > 0") Long id,
                                          @PathVariable @Positive(message = "ID друга должен быть > 0") Long friendId) {
        return userService.addFriend(id, friendId);
    }

    @PatchMapping(path = "/{id}/friends/{friendId}")
    public Map<String, String> confirmFriendship(@PathVariable @Positive(message = "ID должен быть > 0") Long id,
                                          @PathVariable @Positive(message = "ID друга должен быть > 0") Long friendId) {
        return userService.confirmFriendship(id, friendId);
    }

    @DeleteMapping(path = "/{id}/friends/{friendId}")
    public Map<String, String> deleteUser(@PathVariable @Positive(message = "ID должен быть > 0") Long id,
                                          @PathVariable @Positive(message = "ID друга должен быть > 0") Long friendId) {
        return userService.deleteFriend(id, friendId);
    }

}
