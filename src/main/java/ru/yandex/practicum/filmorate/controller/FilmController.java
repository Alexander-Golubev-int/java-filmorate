package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundDataException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.Create;
import ru.yandex.practicum.filmorate.validator.Update;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Validated(Create.class) @RequestBody Film film) {

        if (films.containsValue(film)) {
            log.warn("Попытка добавить уже существующий фильм {}", film);
            throw new DuplicatedDataException("Такой фильм уже существует");
        }

        if (dateValidator(film.getReleaseDate())) {
            log.error("Попытка добавить фильм раньше минимальной даты созданного первого фильма");
            throw new ValidationException("Дата релиза — не может быть раньше 28 декабря 1895 года");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм {} успешно создан и добавлен в список", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Validated(Update.class) @RequestBody Film film) {
        if (film == null) {
            log.warn("Отправлен не проинициализированный фильм");
            throw new NotFoundDataException("Необходимо отправить корректный json формат фильма");
        }

        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с ID = {} уже существует", film.getId());
            throw new NotFoundDataException("ID с таким фильмом не существует");
        }

        if (films.containsValue(film)) {
            log.warn("Попытка добавить уже существующий фильм {}", film);
            throw new DuplicatedDataException("Такой фильм уже есть");
        }

        if (dateValidator(film.getReleaseDate())) {
            log.warn("Попытка добавить фильм раньше минимальной даты созданного первого фильма");
            throw new ValidationException("Дата релиза — не может быть раньше 28 декабря 1895 года");
        }
        Film newFilm = films.get(film.getId());
        if (film.getName() != null) {
            log.info("Фильм {} с названием {} изменен на {}",newFilm, newFilm.getName(), film.getName());
            newFilm.setName(film.getName());
        }

        if (film.getDescription() != null) {
            log.info("Фильм {} изменил описание с {} на {}", newFilm, newFilm.getDescription(), film.getDescription());
            newFilm.setDescription(film.getDescription());
        }

        if (film.getReleaseDate() != null) {
            log.info("Фильм {} изменил дату выпуска с {} на {}", newFilm, newFilm.getReleaseDate(),
                    film.getDescription());
            newFilm.setReleaseDate(film.getReleaseDate());
        }

        if (film.getDuration() != null) {
            log.info("Фильм {} изменил дату продолжительности с {} на {}", newFilm, newFilm.getDuration(),
                    film.getDuration());
            newFilm.setDuration(film.getDuration());
        }
        films.replace(newFilm.getId(), newFilm);
        return newFilm;
    }


    private boolean dateValidator(LocalDate localDate) {
        try {
            return localDate.isBefore(LocalDate.of(1895, 12, 28));
        } catch (Exception e) {
            log.error("Передан неправильный формат даты для фильма");
            throw new ValidationException("Передана неправильная строчка даты. Должен быть формат: yyyy-mm-dd");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
