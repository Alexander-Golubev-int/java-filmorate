package ru.yandex.practicum.filmorate.storage.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FriendshipAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final InMemoryUserStorage inMemoryUserStorage;

    public Map<String, String> addFriend(Long id, Long friendId) {
        checkUserOrThrow(id, friendId);

        User user = inMemoryUserStorage.getUsersMap().get(id);
        if (user.getFriends().contains(friendId)) {
            log.warn("Попытка добавить в друзья пользователя, который уже находится в списке друзей");
            throw new FriendshipAlreadyExistsException(id, friendId);
        }

        user.getFriends().add(friendId);
        user = inMemoryUserStorage.getUsersMap().get(friendId);
        user.getFriends().add(id);
        return Map.of("message", "Друг добавлен");
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
        return user.getFriends().stream()
                .map(friendId -> inMemoryUserStorage.getUsersMap().get(friendId))
                .toList();
    }

    public Collection<User> getMutualFriends(Long id, Long otherId) {
        checkUserOrThrow(id);
        checkUserOrThrow(otherId);
        User user = inMemoryUserStorage.getUsersMap().get(id);
        User otherUser = inMemoryUserStorage.getUsersMap().get(otherId);

        return user.getFriends().stream()
                .filter(friend -> otherUser.getFriends().contains(friend))
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
