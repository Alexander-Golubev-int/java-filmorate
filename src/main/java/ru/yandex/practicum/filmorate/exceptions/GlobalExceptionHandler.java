package ru.yandex.practicum.filmorate.exceptions;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicatedDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDuplicatedData(DuplicatedDataException e) {
        final Map<String, String> error = new HashMap<>();
        error.put("error" , e.getMessage());
        return error;
    }

    @ExceptionHandler(NotFoundDataException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundDataException(NotFoundDataException e) {
        final Map<String, String> error = new HashMap<>();
        error.put("error" , e.getMessage());
        return error;
    }

    @ExceptionHandler(ru.yandex.practicum.filmorate.exceptions.ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationException(ValidationException e) {
        final Map<String, String> error = new HashMap<>();
        error.put("error" , e.getMessage());
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Собираем все ошибки полей и их сообщения
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName =  ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }
}
