package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friendship {
    private long fromUser;
    private long toUser;
    private FriendshipStatus status;
}
