package ru.yandex.practicum.filmorate.storage.dal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.Update;

@Data
public class UpdateUserRequestDto {
    @NotNull(groups = Update.class, message = "Необходимо указать id пользователя")
    private Long id;
    @Email(groups = {Update.class}, regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Неправильно указан email")
    private String email;
    private String login;
    private String name;
    
    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }
    
    public boolean hasLogin() {
        return login != null && !login.isBlank();
    }
    
    public boolean hasName() {
        return name != null && !name.isBlank();
    }
}