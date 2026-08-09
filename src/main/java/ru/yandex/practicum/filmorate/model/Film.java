package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.annotation.DateNotBefore;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundDataException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.validator.Create;
import ru.yandex.practicum.filmorate.validator.Update;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Data
public class Film {
    @NotNull(groups = Update.class, message = "Необходимо указать id фильма")
    private Long id;
    @NotBlank(groups = Create.class, message = "Название фильма не может быть пустым")
    private String name;
    @Size(groups = {Create.class, Update.class}, max = 200, message = "Максимальная длинна описания 200 символов")
    @NotBlank(groups = Create.class, message = "Необходимо указать описание фильма")
    private String description;
    @NotNull(groups = Create.class, message = "Необходимо указать дату создания фильма yyyy-mm-dd")
    @DateNotBefore(groups = {Create.class, Update.class})
    private LocalDate releaseDate;
    @NotNull(groups = Create.class, message = "Продолжительность должна быть указана")
    @Min(groups = {Create.class}, value = 1, message = "Продолжительность должна быть положительным числом")
    private Integer duration;
    private Integer likes = 0;
    private Set<Genre> genres = new HashSet<>();
    private AgeRating ageRating;

    public void addLikes() {
        log.info("Лайк добавлен");
        likes++;
    }

    public void reduceAmountOfLikes() {
        if (likes == 0) {
            return;
        }
        log.info("Лайк успешно удален");
        likes--;
    }

    public void deleteGenre(String genre) {
        if (genre == null || genre.isBlank()) {
            log.info("Отправлен пустой запрос по изменению жанра.");
            throw new ValidationException("Отправлен пустой запрос. Пожалуйста, укажите жанр.");
        }
        try {
            Genre tempGenre = Genre.valueOf(genre.toUpperCase());
            if (!genres.remove(tempGenre)) {
                log.info("Жанра: {} в фильме нет", genre);
                throw new NotFoundDataException("Такого жанра в указанном фильме не существует.");
            }
        } catch (IllegalArgumentException e) {
            log.info("Такой жанр: {} не существует.", genre);
            throw new NotFoundDataException("Жанр " + genre + "' не существует.");
        }
    }

    public void addNewGenre(String genre) {
        if (genre == null || genre.isBlank()) {
            log.info("Отправлен пустой запрос по изменению жанра.");
            throw new ValidationException("Отправлен пустой запрос. Пожалуйста, укажите жанр.");
        }
        try {
            Genre tempGenre = Genre.valueOf(genre.toUpperCase());
            if (genres.contains(tempGenre)) {
                log.info("Такой жанр({})уже есть для указанного фильма.", genre);
                throw new DuplicatedDataException("Такой жанр уже есть для указанного фильма.");
            } else {
                log.info("Жанр: {} успешно добавлен к фильму", tempGenre);
                genres.add(tempGenre);
            }
        } catch (IllegalArgumentException e) {
            log.info("Жанр {} не существует.", genre);
            throw new NotFoundDataException("Жанр " + genre + " не существует.");
        }
    }

    public void addNewRating(String rating) {
        if (rating == null || rating.isBlank()) {
            log.info("Отправлен пустой запрос по изменению рейтинга.");
            throw new ValidationException("Отправлен пустой запрос. Пожалуйста, укажите рейтинг.");
        }
        try {
            ageRating = AgeRating.valueOf(rating.toUpperCase());
            log.info("Рейтинг успешно изменен.");
        } catch (IllegalArgumentException e) {
            log.info("Рейтинг {} не существует.", rating);
            throw new NotFoundDataException("Рейтинг " + rating + " не существует.");
        }
    }
}

