package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
public class ErrorResponse {
    private String type = "error";
    private String description;
    private LocalDateTime localDateTime;

    public ErrorResponse() {
        localDateTime = LocalDateTime.now();
    }

    public ErrorResponse(String description) {
        this.description = description;
        localDateTime = LocalDateTime.now();
    }

}
