package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friendship {
    private long fromUser;
    private long toUser;
    private FriendshipStatus status;

    public Friendship(long fromUser, long toUser, FriendshipStatus status) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.status = status;
    }

    public Friendship(FriendshipStatus status) {
        this.status = status;
    }
}
