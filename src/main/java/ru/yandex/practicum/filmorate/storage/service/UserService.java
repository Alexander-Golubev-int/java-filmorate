package ru.yandex.practicum.filmorate.storage.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.FriendshipAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validator.Create;
import ru.yandex.practicum.filmorate.validator.Update;

import java.util.Collection;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final InMemoryUserStorage inMemoryUserStorage;

    @GetMapping
    public Collection<User> getUsers() {
        return inMemoryUserStorage.getUsers();
    }

    @PostMapping
    public User createUser(@Validated(Create.class) @RequestBody User user) {
        return inMemoryUserStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@Validated(Update.class) @RequestBody User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    public Map<String, String> addFriend(Long id, Long friendId) {
        checkUserOrThrow(id, friendId);

        User user = inMemoryUserStorage.getUsersMap().get(id);
        if (user.getFriends().containsKey(friendId)) {
            log.warn("Попытка добавить в друзья пользователя, который уже находится в списке друзей");
            throw new FriendshipAlreadyExistsException(id, friendId);
        }

        user.getFriends().put(friendId, FriendshipStatus.PENDING);
        user = inMemoryUserStorage.getUsersMap().get(friendId);
        user.getFriends().put(id, FriendshipStatus.PENDING);
        return Map.of("message", "Друг добавлен");
    }

    public Map<String, String> confirmFriendship(Long id, Long friendId) {
        checkUserOrThrow(id, friendId);

        User user = inMemoryUserStorage.getUsersMap().get(id);
        if (!user.getFriends().containsKey(friendId)) {
            log.warn("Попытка подтвердить дружбу когда пользователи еще не друзья");
            throw new NotFoundDataException("Попытка подтвердить дружбу когда пользователи еще не друзья");
        }

        user.getFriends().put(friendId, FriendshipStatus.ACCEPTED);
        user = inMemoryUserStorage.getUsersMap().get(friendId);
        user.getFriends().put(id, FriendshipStatus.ACCEPTED);
        return Map.of("message", "Дружба подтверждена!😎");
    }

    public Map<String, String> deleteFriend(Long id, Long friendId) {
        checkUserOrThrow(id, friendId);

        User user = inMemoryUserStorage.getUsersMap().get(id);
        user.getFriends().remove(friendId);
        user = inMemoryUserStorage.getUsersMap().get(friendId);
        user.getFriends().remove(id);
        return Map.of("message", "Пользователь успешно удален");
    }

    public Collection<User> getFriendsUser(Long id) {
        checkUserOrThrow(id);
        User user = inMemoryUserStorage.getUsersMap().get(id);
        return user.getFriends().keySet().stream()
                .map(friendId -> inMemoryUserStorage.getUsersMap().get(friendId))
                .toList();
    }

    public Collection<User> getMutualFriends(Long id, Long otherId) {
        checkUserOrThrow(id);
        checkUserOrThrow(otherId);
        User user = inMemoryUserStorage.getUsersMap().get(id);
        User otherUser = inMemoryUserStorage.getUsersMap().get(otherId);

        return user.getFriends().keySet().stream()
                .filter(friend -> otherUser.getFriends().containsKey(friend))
                .map(friend -> inMemoryUserStorage.getUsersMap().get(friend))
                .toList();
    }

    private void checkUserOrThrow(Long id, Long friendId) {
        if (!inMemoryUserStorage.getUsersKeySet().contains(id)) {
            log.warn("Отправлен не проинициализированный пользователь при попытке добавить в друзья: {}", id);
            throw new NotFoundDataException("Отправлен не проинициализированный пользователь: " + id);
        }

        if (!inMemoryUserStorage.getUsersKeySet().contains(friendId)) {
            log.warn("Отправлен не проинициализированный пользователь при попытке добавить в друзья: {}", friendId);
            throw new NotFoundDataException("Отправлен не проинициализированный пользователь: " + friendId);
        }
    }

    private void checkUserOrThrow(Long id) {
        if (!inMemoryUserStorage.getUsersKeySet().contains(id)) {
            log.warn("Отправлен не проинициализированный пользователь: {}", id);
            throw new NotFoundDataException("Отправлен не проинициализированный пользователь: " + id);
        }
    }

}
