package ru.yandex.practicum.filmorate.exceptions;

public class FriendshipAlreadyExistsException extends RuntimeException {
    public FriendshipAlreadyExistsException(Long id, Long friendId) {
        super(String.format("Пользователи %d и %d уже являются друзьями", id, friendId));
    }
}