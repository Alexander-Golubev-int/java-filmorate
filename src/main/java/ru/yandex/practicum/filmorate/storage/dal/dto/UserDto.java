package ru.yandex.practicum.filmorate.storage.dal.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private String login;
    private LocalDate birthday;
}
