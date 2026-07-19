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
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    void getEmptyListFilms() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void createFilmWithNameNull() throws Exception {
        String json = """
                {
                    "name": null,
                    "description":"Описание",
                    "releaseDate":"2025-12-12",
                    "duration":20
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("name"))
                .andExpect(jsonPath("$.description").value("Название фильма не может быть пустым"));
    }

    @Test
    @Order(3)
    void createFilmWithNameEmpty() throws Exception {
        String json = """
                {
                    "name": "",
                    "description":"Описание",
                    "releaseDate":"2025-12-12",
                    "duration":20
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("name"))
                .andExpect(jsonPath("$.description").value("Название фильма не может быть пустым"));
    }

    @Test
    @Order(4)
    void createFilmWithCorrectName() throws Exception {
        String json = """
                {
                    "name": "Муся",
                    "description":"Описание",
                    "releaseDate":"2025-12-12",
                    "duration":20
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Муся"))
                .andExpect(jsonPath("$.description").value("Описание"))
                .andExpect(jsonPath("$.releaseDate").value("2025-12-12"))
                .andExpect(jsonPath("$.duration").value("20"));
    }

    @Test
    @Order(5)
    void createFilmDescriptionNull() throws Exception {
        String json = """
                {
                    "name": "Муся",
                    "description": null,
                    "releaseDate":"2025-12-12",
                    "duration":20
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("description"))
                .andExpect(jsonPath("$.description").value("Необходимо указать описание фильма"));
    }

    @Test
    @Order(6)
    void createFilmDescriptionEmpty() throws Exception {
        String json = """
                {
                    "name": "Муся",
                    "releaseDate":"2025-12-12",
                    "duration":20
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("description"))
                .andExpect(jsonPath("$.description").value("Необходимо указать описание фильма"));
    }

    @Test
    @Order(7)
    void createFilmDescriptionWithSize201() throws Exception {
        String json = """
                {
                     "name": "Муся",
                     "description": "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec qua",
                     "releaseDate":"2025-12-12",
                     "duration":20
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("description"))
                .andExpect(jsonPath("$.description").value("Максимальная длинна описания 200 символов"));
    }

    @Test
    @Order(8)
    void createFilmDescriptionWithSize200() throws Exception {
        String json = """
                {
                     "name": "Муся",
                     "description": "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec qu",
                     "releaseDate":"2025-12-12",
                     "duration":20
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Муся"))
                .andExpect(jsonPath("$.description").value("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec qu"))
                .andExpect(jsonPath("$.releaseDate").value("2025-12-12"))
                .andExpect(jsonPath("$.duration").value("20"));
    }

    @Test
    @Order(9)
    void createFilmReleaseDateNull() throws Exception {
        String json = """
                {
                    "name": "Муся",
                    "description": "Java",
                    "releaseDate": null,
                    "duration":20
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("releaseDate"))
                .andExpect(jsonPath("$.description").value("Необходимо указать дату создания фильма yyyy-mm-dd"));
    }

    @Test
    @Order(10)
    void createFilmReleaseDateSimpleText() throws Exception {
        String json = """
                {
                    "name": "Муся",
                    "description": "Java",
                    "releaseDate": "Work again?",
                    "duration":20
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("error"))
                .andExpect(jsonPath("$.description").value("Некорректный JSON или неверный формат данных"));
    }

    @Test
    @Order(11)
    void createFilmReleaseDateUnder1895() throws Exception {
        String json = """
                {
                    "name": "Муся",
                    "description": "Java",
                    "releaseDate": "1894-12-28",
                    "duration":20
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("releaseDate"))
                .andExpect(jsonPath("$.description").value("Указанная дата раньше 1895-12-28"));
    }

    @Test
    @Order(12)
    void createFilmReleaseDateEqual1895() throws Exception {
        String json = """
                {
                    "name": "Муся",
                    "description": "Java",
                    "releaseDate": "1895-12-28",
                    "duration":20
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Муся"))
                .andExpect(jsonPath("$.description").value("Java"))
                .andExpect(jsonPath("$.releaseDate").value("1895-12-28"))
                .andExpect(jsonPath("$.duration").value("20"));
    }

    @Test
    @Order(13)
    void createFilmReleaseDurationNull() throws Exception {
        String json = """
                {
                    "name": "Муся",
                    "description": "Java",
                    "releaseDate": "1895-12-28",
                    "duration": null
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("duration"))
                .andExpect(jsonPath("$.description").value("Продолжительность должна быть указана"));
    }

    @Test
    @Order(14)
    void createFilmReleaseDurationSimpleText() throws Exception {
        String json = """
                {
                    "name": "Муся",
                    "description": "Java",
                    "releaseDate": "1895-12-28",
                    "duration": "Java"
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("error"))
                .andExpect(jsonPath("$.description").value("Некорректный JSON или неверный формат данных"));
    }

    @Test
    @Order(15)
    void createFilmReleaseDurationZero() throws Exception {
        String json = """
                {
                    "name": "Муся",
                    "description": "Java",
                    "releaseDate": "1895-12-28",
                    "duration": 0
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("duration"))
                .andExpect(jsonPath("$.description").value("Продолжительность должна быть положительным числом"));
    }

    @Test
    @Order(16)
    void createFilmReleaseDuration1() throws Exception {
        String json = """
                {
                    "name": "Муся",
                    "description": "Java",
                    "releaseDate": "1895-12-28",
                    "duration": 1
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("Муся"))
                .andExpect(jsonPath("$.description").value("Java"))
                .andExpect(jsonPath("$.releaseDate").value("1895-12-28"))
                .andExpect(jsonPath("$.duration").value("1"));
    }

    @Test
    @Order(17)
    void updateFilmRelease() throws Exception {
        String json = """
                {
                    "id": 4, 
                    "name": "Муся наносит ответный удар",
                    "description": "Java",
                    "releaseDate": "1895-12-28",
                    "duration": 1
                }
                """;
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("Муся наносит ответный удар"))
                .andExpect(jsonPath("$.description").value("Java"))
                .andExpect(jsonPath("$.releaseDate").value("1895-12-28"))
                .andExpect(jsonPath("$.duration").value("1"));
    }

    @Test
    @Order(18)
    void updateFilmReleaseWithoutId() throws Exception {
        String json = """
                {
                    "name": "Муся наносит ответный удар",
                    "description": "Java",
                    "releaseDate": "1895-12-28",
                    "duration": 1
                }
                """;
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("id"))
                .andExpect(jsonPath("$.description").value("Необходимо указать id фильма"));
    }

    @Test
    @Order(19)
    void updateFilmReleaseWithoutNonExistentId() throws Exception {
        String json = """
                {  
                    "id": 999,
                    "name": "Муся наносит ответный удар",
                    "description": "Java",
                    "releaseDate": "1895-12-28",
                    "duration": 1
                }
                """;
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("error"))
                .andExpect(jsonPath("$.description").value("ID с таким фильмом не существует"));
    }

    @Test
    @Order(20)
    void updateFilmReleaseWhereIdIsSimpleText() throws Exception {
        String json = """
                {  
                    "id": "Jav",
                    "name": "Муся наносит ответный удар",
                    "description": "Java",
                    "releaseDate": "1895-12-28",
                    "duration": 1
                }
                """;
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("error"))
                .andExpect(jsonPath("$.description").value("Некорректный JSON или неверный формат данных"));
    }

    @Test
    @Order(21)
    void updateFilmWithoutChanges() throws Exception {
        String json = """
                {  
                    "id": "4",
                    "name": "Муся наносит ответный удар",
                    "description": "Java",
                    "releaseDate": "1895-12-28",
                    "duration": 1
                }
                """;
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("error"))
                .andExpect(jsonPath("$.description").value("Такой фильм уже есть"));
    }

    @Test
    @Order(22)
    void getNotListFilms() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Муся"))
                .andExpect(jsonPath("$[3].duration").value(1));
    }
}