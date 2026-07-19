package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    void getEmptyListUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void createUserWithEmailNull() throws Exception {
        String json = "{\"email\":null,\"login\":\"Billy\",\"name\":\"Herrington\",\"birthday\":\"1964-07-14\"}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("email"))
                .andExpect(jsonPath("$.description").value("Email не должен быть пустым"));
    }

    @Test
    @Order(3)
    void createUserWithEmailEmpty() throws Exception {
        String json = "{\"email\":\" \",\"login\":\"Billy\",\"name\":\"Herrington\",\"birthday\":\"1964-07-14\"}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("email"))
                .andExpect(jsonPath("$.description").value("Неправильно указан email"));
    }

    @Test
    @Order(4)
    void createUserWithEmailWithoutDog() throws Exception {
        String json = "{\"email\":\"bobamail.ru\",\"login\":\"Billy\",\"name\":\"Herrington\",\"birthday\":\"1964-07-14\"}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("email"))
                .andExpect(jsonPath("$.description").value("Неправильно указан email"));
    }

    @Test
    @Order(5)
    void createUserWithCorrectEmail() throws Exception {
        String json = "{\"email\":\"bobmarli@mail.ru\",\"login\":\"Billy\",\"name\":\"Herrington\",\"birthday\":\"1964-07-14\"}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("bobmarli@mail.ru"))
                .andExpect(jsonPath("$.login").value("Billy"))
                .andExpect(jsonPath("$.name").value("Herrington"))
                .andExpect(jsonPath("$.birthday").value("1964-07-14"));
    }

    @Test
    @Order(6)
    void createUserWithLoginNull() throws Exception {
        String json = "{\"email\":\"bobmarli@mail.ru\",\"login\":null,\"name\":\"Herrington\",\"birthday\":\"1964-07-14\"}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("login"))
                .andExpect(jsonPath("$.description").value("Логин не должен быть пустым"));
    }

    @Test
    @Order(7)
    void createUserWithLoginEmpty() throws Exception {
        String json = "{\"email\":\"bobmarli@mail.ru\",\"login\":\" \",\"name\":\"Herrington\",\"birthday\":\"1964-07-14\"}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("login"))
                .andExpect(jsonPath("$.description").value("Логин должен содержать только буквы и без пробелов"));
    }

    @Test
    @Order(8)
    void createUserWithLoginHaveSpace() throws Exception {
        String json = "{\"email\":\"bobmarli@mail.ru\",\"login\":\"Bob Bip \",\"name\":\"Herrington\",\"birthday\":\"1964-07-14\"}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("login"))
                .andExpect(jsonPath("$.description").value("Логин должен содержать только буквы и без пробелов"));
    }

    @Test
    @Order(9)
    void createUserWithCorrectLogin() throws Exception {
        String json = "{\"email\":\"bobamarli@mail.ru\",\"login\":\"BobBippp\",\"name\":\"Herringtonn\",\"birthday\":\"1964-07-14\"}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("bobamarli@mail.ru"))
                .andExpect(jsonPath("$.login").value("BobBippp"))
                .andExpect(jsonPath("$.name").value("Herringtonn"))
                .andExpect(jsonPath("$.birthday").value("1964-07-14"));
    }

    @Test
    @Order(10)
    void createUserWithBirthdayNull() throws Exception {
        String json = "{\"email\":\"bobmarli@mail.ru\",\"login\":\"BobBip\",\"name\":\"Herrington\",\"birthday\":null}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("birthday"))
                .andExpect(jsonPath("$.description").value("Дата дня рождения не может быть null"));
    }

    @Test
    @Order(11)
    void createUserWithBirthdayWithSimpleText() throws Exception {
        String json = "{\"email\":\"bobmarli@mail.ru\",\"login\":\"BobBip\",\"name\":\"Herrington\",\"birthday\":\"koko\"}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("error"))
                .andExpect(jsonPath("$.description").value("Некорректный JSON или неверный формат данных"));
    }

    @Test
    @Order(12)
    void createUserWithFutureBirthday() throws Exception {
        String json = "{\"email\":\"bobmarli@mail.ru\",\"login\":\"BobBip\",\"name\":\"Herrington\",\"birthday\":\"2027-07-19\"}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("birthday"))
                .andExpect(jsonPath("$.description").value("Дата дня рождения не может быть в будущем"));
    }

    @Test
    @Order(13)
    void createUserWithCorrectBirthday() throws Exception {
        String json = "{\"email\":\"piterParker@mail.ru\",\"login\":\"BobBip\",\"name\":\"Herrington\",\"birthday\":\"2025-07-19\"}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("piterParker@mail.ru"))
                .andExpect(jsonPath("$.login").value("BobBip"))
                .andExpect(jsonPath("$.name").value("Herrington"))
                .andExpect(jsonPath("$.birthday").value("2025-07-19"));
    }

    @Test
    @Order(14)
    void createUserWithoutName() throws Exception {
        String json = "{\"email\":\"norm@mail.ru\",\"login\":\"BobBip\",\"birthday\":\"2025-07-19\"}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.email").value("norm@mail.ru"))
                .andExpect(jsonPath("$.login").value("BobBip"))
                .andExpect(jsonPath("$.name").value("BobBip"))
                .andExpect(jsonPath("$.birthday").value("2025-07-19"));
    }

    @Test
    @Order(15)
    void updateUserWithoutChangingData() throws Exception {
        String json = "{\"id\":2,\"email\":\"bobamarli@mail.ru\",\"login\":\"BobBippp\",\"name\":\"Herringtonn\",\"birthday\":\"1964-07-14\"}";
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("error"))
                .andExpect(jsonPath("$.description").value("Данные не изменяются"));
    }

    @Test
    @Order(16)
    void updateUserWithNonExistId() throws Exception {
        String json = "{\"id\":404,\"email\":\"bobmarli@mail.ru\",\"login\":\"BobBip\",\"name\":\"BobBip\",\"birthday\":\"2025-07-19\"}";
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("error"))
                .andExpect(jsonPath("$.description").value("ID с таким пользователем не существует"));
    }

    @Test
    @Order(17)
    void updateUserWithout() throws Exception {
        String json = "{\"email\":\"bobmarli@mail.ru\",\"login\":\"BobBip\",\"name\":\"BobBip\",\"birthday\":\"2025-07-19\"}";
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("id"))
                .andExpect(jsonPath("$.description").value("Необходимо указать id пользователя"));
    }
}