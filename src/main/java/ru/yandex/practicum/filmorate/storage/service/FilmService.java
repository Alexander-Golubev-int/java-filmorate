package ru.yandex.practicum.filmorate.storage.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final InMemoryUserStorage inMemoryUserStorage;
    private final InMemoryFilmStorage inMemoryFilmStorage;


    public Collection<Film> getFilms() {
        return inMemoryFilmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        return inMemoryFilmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    public Map<String, String> addLike(Long filmId, Long userId) {
        checkFilmAndUserOrThrow(filmId, userId);
        throwIfAlreadyLiked(filmId, userId);
        User user = inMemoryUserStorage.getUsersMap().get(userId);
        Film film = inMemoryFilmStorage.getFilmsMap().get(filmId);
        user.getFavoriteFilms().put(filmId, film);
        //film.addLikes();
        return Map.of("message", "лайк успешно добавлен");
    }

    public Map<String, String> deleteLike(Long filmId, Long userId) {
        checkFilmAndUserOrThrow(filmId, userId);
        throwIfLikeDoesNotExist(filmId, userId);
        User user = inMemoryUserStorage.getUsersMap().get(userId);
        Film film = inMemoryFilmStorage.getFilmsMap().get(filmId);
        user.getFavoriteFilms().remove(filmId);
        //film.reduceAmountOfLikes();
        return Map.of("message", "лайк успешно удален");
    }

//    public Collection<Film> getMostFavoriteFilms(Long count) {
//        return inMemoryFilmStorage.getFilms().stream()
//                .sorted(Comparator.comparingInt(Film::getLikes).reversed())
//                .limit(count)
//                .toList();
//    }

    private void throwIfAlreadyLiked(Long filmId, Long userId) {
        User user = inMemoryUserStorage.getUsersMap().get(userId);
        if (user.getFavoriteFilms().containsKey(filmId)) {
            throw new DuplicatedDataException(String.format("Пользователь с id: %d уже поставил лайк фильму с id: %d", userId, filmId));
        }
    }

    private void throwIfLikeDoesNotExist(Long filmId, Long userId) {
        User user = inMemoryUserStorage.getUsersMap().get(userId);
        if (!user.getFavoriteFilms().containsKey(filmId)) {
            throw new NotFoundDataException(String.format("У пользователь с id: %d нет лайка к фильму с id: %d",
                    userId, filmId));
        }
    }

    private void checkFilmAndUserOrThrow(Long filmId, Long userId) {
        if (!inMemoryFilmStorage.getFilmsKeySet().contains(filmId)) {
            log.warn("Отправлен filmId фильма который не существует: {}", filmId);
            throw new NotFoundDataException("Отправлен filmId фильма который не существует: " + filmId);
        }
        checkUserOrThrow(userId);
    }

    private void checkUserOrThrow(Long id) {
        if (!inMemoryUserStorage.getUsersKeySet().contains(id)) {
            log.warn("Отправлен не проинициализированный пользователь: {}", id);
            throw new NotFoundDataException("Отправлен не проинициализированный пользователь: " + id);
        }
    }
}
