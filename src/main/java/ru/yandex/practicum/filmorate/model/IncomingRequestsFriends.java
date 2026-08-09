package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class IncomingRequestsFriends {
    private final Set<Long> incomingUsersSetList = new HashSet<>();
}
