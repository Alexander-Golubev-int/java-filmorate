package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundDataException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        if (films.containsValue(film)) {
            log.warn("Попытка добавить уже существующий фильм {}", film);
            throw new DuplicatedDataException("Такой фильм уже существует");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм {} успешно создан и добавлен в список", film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film == null) {
            log.warn("Отправлен не проинициализированный фильм");
            throw new ValidationException("Необходимо отправить корректный json формат фильма");
        }

        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с ID = {} уже существует", film.getId());
            throw new NotFoundDataException("ID с таким фильмом не существует");
        }

        if (films.containsValue(film)) {
            log.warn("Попытка добавить уже существующий фильм {}", film);
            throw new DuplicatedDataException("Такой фильм уже есть");
        }

        Film newFilm = films.get(film.getId());
        if (film.getName() != null) {
            log.info("Фильм {} с названием {} изменен на {}", newFilm, newFilm.getName(), film.getName());
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

    public Set<Long> getFilmsKeySet() {
        return films.keySet();
    }

    public Map<Long, Film> getFilmsMap() {
        return films;
    }

    private long getNextId() {
        long currentMaxId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
