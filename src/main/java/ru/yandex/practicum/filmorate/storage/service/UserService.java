package ru.yandex.practicum.filmorate.storage.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.FriendshipAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundDataException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserRepository;


import java.util.Collection;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final InMemoryUserStorage inMemoryUserStorage;
    private final UserRepository userRepository;
    //DONE
    public Collection<User> getUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return inMemoryUserStorage.createUser(user);
    } //TO DO

    public User updateUser(User user) {
        return inMemoryUserStorage.updateUser(user);
    } //TO DO
    //DONE
    public Map<String, String> addFriend(Long id, Long friendId) {

        if (id.equals(friendId)) {
            throw new DuplicatedDataException("Пользователь пытается добавить самого себя в друзья");
        }

        checkUserOrThrow(id, friendId);

        boolean isFriend = getFriendsUser(id).stream()
                .anyMatch(user -> user.getId().equals(friendId));
        boolean isHasPendingFriendRequest = userRepository.isFriendRequestPending(id, friendId);
        boolean isAnIncomingRequest = userRepository.isAnIncomingRequest(id, friendId);

        if (isFriend) {
            log.warn("Попытка добавить в друзья пользователя, который уже находится в списке друзей");
            throw new FriendshipAlreadyExistsException(id, friendId);
        } else if (isHasPendingFriendRequest) {
            log.warn("Попытка добавить в друзья пользователя, которому уже была отправлена заявка в друзья");
            throw new DuplicatedDataException(String.format("Вы уже отправляли заявку на добавления в друзья пользователя %s", friendId));
        } else if (isAnIncomingRequest) {
            log.warn("Попытка добавить в друзья пользователя, который отправил пользователю заявку на добавление");
            throw new DuplicatedDataException(String.format("Пользователь %s уже отправил вам заявку на добавления в " +
                    "друзья. Подтвердите добавление или удалить его из друзей.", friendId));
        }

        userRepository.addNewFriend(id, friendId);
        userRepository.addIncomingRequestToFriends(friendId, id);
        log.info("Пользователь {} добавлен в список входящих заявок у пользователя {}", id, friendId);
        return Map.of("message", "Друг добавлен");
    }

    public Map<String, String> confirmFriendship(Long id, Long friendId) {
        checkUserOrThrow(id, friendId);

        User user = inMemoryUserStorage.getUsersMap().get(id);

        if (user.getFriends().containsKey(friendId) && user.getFriends().get(friendId).getStatus() == FriendshipStatus.ACCEPTED) {
            log.info("Пользователь {} уже находятся в дружбе с {}", id, friendId);
            throw new DuplicatedDataException("Пользователь уже находятся в дружбе");
        }

        if (!user.getRequestsFriendsSetList().getIncomingUsersSetList().contains(friendId)) {
            log.warn("Попытка подтвердить дружбу когда пользователи еще не друзья");
            throw new NotFoundDataException("Попытка подтвердить дружбу когда пользователи еще не друзья");
        }

        User friend = inMemoryUserStorage.getUsersMap().get(friendId);
        Friendship friendship = friend.getFriends().get(id);
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        user.getFriends().put(friendId, friendship);
        log.info("Дружба между {} и {} успешно подтверждена.", id, friendId);
        user.getRequestsFriendsSetList().getIncomingUsersSetList().remove(friendId);
        log.info("Пользователь {} удален из списка входящих заявок на добавление", friendId);
        return Map.of("message", "Дружба подтверждена!😎");
    }

    public Map<String, String> deleteFriend(Long id, Long friendId) {
        checkUserOrThrow(id, friendId);
        User user = inMemoryUserStorage.getUsersMap().get(id);
        if (user.getRequestsFriendsSetList().getIncomingUsersSetList().contains(friendId)) {
            user.getRequestsFriendsSetList().getIncomingUsersSetList().remove(friendId);
            return Map.of("message", "Пользователь успешно удален");
        }
        user.getFriends().remove(friendId);

        user = inMemoryUserStorage.getUsersMap().get(friendId);
        if (user.getRequestsFriendsSetList().getIncomingUsersSetList().contains(id)) {
            user.getRequestsFriendsSetList().getIncomingUsersSetList().remove(id);
            return Map.of("message", "Пользователь успешно удален");
        }
        user.getFriends().remove(id);
        return Map.of("message", "Пользователь успешно удален");
    }
    //DONE
    public Collection<User> getFriendsUser(Long id) {
        checkUserOrThrow(id);
        return userRepository.findAllFriends(id);
    }

    public Collection<User> getMutualFriends(Long id, Long otherId) {
        checkUserOrThrow(id);
        checkUserOrThrow(otherId);
        User user = inMemoryUserStorage.getUsersMap().get(id);
        User otherUser = inMemoryUserStorage.getUsersMap().get(otherId);

        return user.getFriends().entrySet().stream()
                .filter(entry -> otherUser.getFriends().containsKey(entry.getKey()) && otherUser.getFriends().get(entry.getKey()).getStatus() == FriendshipStatus.ACCEPTED &&
                        user.getFriends().get(entry.getKey()).getStatus() == FriendshipStatus.ACCEPTED)
                .map(entry -> inMemoryUserStorage.getUsersMap().get(entry.getKey()))
                .toList();
    }
    //DONE
    private void checkUserOrThrow(Long id, Long friendId) {
        if (userRepository.findById(id).isEmpty()) {
            log.warn("Отправлен не проинициализированный пользователь при попытке добавить в друзья: {}", id);
            throw new NotFoundDataException("Отправлен не проинициализированный пользователь: " + id);
        }

        if (userRepository.findById(friendId).isEmpty()) {
            log.warn("Отправлен не проинициализированный пользователь при попытке добавить в друзья: {}", friendId);
            throw new NotFoundDataException("Отправлен не проинициализированный пользователь: " + friendId);
        }
    }
    //DONE
    private void checkUserOrThrow(Long id) {
        if(userRepository.findById(id).isEmpty()) {
            log.warn("Отправлен не проинициализированный пользователь: {}", id);
            throw new NotFoundDataException("Отправлен не проинициализированный пользователь: " + id);
        }
    }

}
