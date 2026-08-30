package ru.yandex.practicum.filmorate.storage.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.FriendshipAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundDataException;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.dal.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.storage.dal.dto.UpdateUserRequestDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.dal.repository.UserRepository;


import java.util.Collection;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    //DONE
    public Collection<UserDto> getUsers() {
        return userRepository.findAll();
    }
    //DONE
    public UserDto createUser(NewUserRequest user) {
        return userRepository.addNewUser(user);
    }
    //DONE
    public UserDto updateUser(UpdateUserRequestDto user) {
        checkUserOrThrow(user.getId());
        return userRepository.updateUser(user);
    }
    //DONE
    public Map<String, String> addFriend(Long id, Long friendId) {

        if (id.equals(friendId)) {
            throw new DuplicatedDataException("Пользователь пытается добавить самого себя в друзья");
        }

        checkUserOrThrow(id, friendId);

        boolean areFriends = getFriendsUser(id).stream()
                .anyMatch(user -> user.getId().equals(friendId));
        boolean isHasPendingFriendRequest = userRepository.isFriendRequestPending(id, friendId);
        boolean isAnIncomingRequest = userRepository.isAnIncomingRequest(id, friendId);

        if (areFriends) {
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
    //DONE
    public Map<String, String> confirmFriendship(Long id, Long friendId) {
        checkUserOrThrow(id, friendId);

        boolean areFriends = userRepository.areFriends(id, friendId);
        boolean haveIncomingRequest = !userRepository.isAnIncomingRequest(id, friendId);

        if (areFriends) {
            log.info("Пользователь {} уже находятся в дружбе с {}", id, friendId);
            throw new DuplicatedDataException("Пользователь уже находятся в дружбе");
        }

        if (haveIncomingRequest) {
            log.warn("Попытка подтвердить дружбу когда пользователи еще не друзья {} -> {}", id, friendId);
            throw new NotFoundDataException("Попытка подтвердить дружбу когда пользователи еще не друзья");
        }

        userRepository.confirmFriendship(friendId, id);
        userRepository.addNewFriendAfterConfirmFriendship(id, friendId);
        log.info("Дружба между {} и {} успешно подтверждена.", id, friendId);
        userRepository.deleteIncomingRequestToFriends(id, friendId);
        log.info("Пользователь {} удален из списка входящих заявок на добавление", friendId);
        return Map.of("message", "Дружба подтверждена!😎");
    }
    //DONE
    public Map<String, String> deleteFriend(Long id, Long friendId) {
        checkUserOrThrow(id, friendId);

        boolean areFriends = getFriendsUser(id).stream()
                .noneMatch(user -> user.getId().equals(friendId));
        boolean hasIncomingRequest = userRepository.isAnIncomingRequest(id, friendId);
        if (areFriends && !hasIncomingRequest) {
            log.info("Пользователь {} не состоит в дружбе с пользователем {} удаление невозможно", id, friendId);
            throw new NotFoundDataException("Пользователи не состоят в дружбе");
        }

        if (hasIncomingRequest) {
            userRepository.deleteIncomingRequestToFriends(id, friendId);
            userRepository.deleteFriendships(friendId, id);
            return Map.of("message", "Заявка на принятия дружбы отклонена");

        }
        userRepository.deleteFriendships(id, friendId);
        userRepository.deleteFriendships(friendId, id);
        log.info("Пользователь {} удалил пользователя {} из друзей", id, friendId);
        return Map.of("message", "Пользователь успешно удален");
    }
    //DONE
    public Collection<UserDto> getFriendsUser(Long id) {
        checkUserOrThrow(id);
        return userRepository.findAllFriends(id);
    }
    //DONE
    public Collection<UserDto> getMutualFriends(Long id, Long otherId) {
        checkUserOrThrow(id);
        checkUserOrThrow(otherId);
        return userRepository.getCommonFriends(id, otherId);
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
