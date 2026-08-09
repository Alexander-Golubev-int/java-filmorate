package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.DateNotBefore;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundDataException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.validator.Create;
import ru.yandex.practicum.filmorate.validator.Update;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    private Set<Genre> genreSet = new HashSet<>();
    private Set<AgeRating> ageRatings = new HashSet<>();

    public void addLikes() {
        likes++;
    }

    public void reduceAmountOfLikes() {
        if (likes == 0) {
            return;
        }
        likes--;
    }

    public void deleteGenre(String genre) {
        if (genre == null || genre.isBlank()) {
            throw new ValidationException("Отправлен пустой запрос. Пожалуйста, укажите жанр.");
        }
        try {
            Genre tempGenre = Genre.valueOf(genre.toUpperCase());
            if (!genreSet.remove(tempGenre)) {
                throw new NotFoundDataException("Такого жанра в указанном фильме не существует.");
            }
        } catch (IllegalArgumentException e) {
            throw new NotFoundDataException("Жанр " + genre + "' не существует.");
        }
    }

    public void addNewGenre(String genre) {
        if (genre == null || genre.isBlank()) {
            throw new ValidationException("Отправлен пустой запрос. Пожалуйста, укажите жанр.");
        }
        try {
            Genre tempGenre = Genre.valueOf(genre.toUpperCase());
            if (genreSet.contains(tempGenre)) {
                throw new DuplicatedDataException("Такой жанр уже есть для указанного фильма.");
            } else {
                genreSet.add(tempGenre);
            }
        } catch (IllegalArgumentException e) {
            throw new NotFoundDataException("Жанр " + genre + " не существует.");
        }
    }

    public void deleteAgeRating(String rating) {
        if (rating == null || rating.isBlank()) {
            throw new ValidationException("Отправлен пустой запрос. Пожалуйста, укажите рейтинг.");
        }
        try {
            AgeRating tempAgeRating = AgeRating.valueOf(rating.toUpperCase());
            if (!ageRatings.remove(tempAgeRating)) {
                throw new NotFoundDataException("Такого рейтинга нет в указанном фильме.");
            }
        } catch (IllegalArgumentException e) {
            throw new NotFoundDataException("Рейтинг: " + rating + " не существует.");
        }
    }

    public void addNewRating(String rating) {
        if (rating == null || rating.isBlank()) {
            throw new ValidationException("Отправлен пустой запрос. Пожалуйста, укажите рейтинг.");
        }
        try {
            AgeRating tempAgeRating = AgeRating.valueOf(rating.toUpperCase());
            if (ageRatings.contains(tempAgeRating)) {
                throw new DuplicatedDataException("Такой рейтинг уже есть для указанного фильма.");
            } else {
                ageRatings.add(tempAgeRating);
            }
        } catch (IllegalArgumentException e) {
            throw new NotFoundDataException("Рейтинг " + rating + " не существует.");
        }
    }
}
