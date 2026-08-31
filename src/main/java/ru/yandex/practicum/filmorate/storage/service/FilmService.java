package ru.yandex.practicum.filmorate.storage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundDataException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.dal.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.GenreDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.dal.dto.UpdateFilmRequestDto;
import ru.yandex.practicum.filmorate.storage.dal.repository.FilmRepository;
import ru.yandex.practicum.filmorate.storage.dal.repository.UserRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserService userService;
    //DONE
    public Collection<FilmDto> getFilms() {
        return filmRepository.findAll();
    }

    public Collection<GenreDto> getGenre() {
        return filmRepository.findAllGenre();
    }

    public GenreDto getGenreId(Long id) {
        return filmRepository.findGenreById(id);
    }
    //DONE
    public FilmDto createFilm(NewFilmRequest film) {
        return filmRepository.addNewFilm(film);
    }
    //DONE
    public FilmDto updateFilm(UpdateFilmRequestDto film) {
        try {
            checkFilmOrThrow(film.getId());
        } catch (NullPointerException e) {
            throw new ValidationException("Отправлен некорректный JSON");
        }
        return filmRepository.updateFilm(film);
    }
    //DONE
    public Map<String, String> addLike(Long filmId, Long userId) {
        checkFilmOrThrow(filmId);
        userService.checkUserOrThrow(userId);
        throwIfAlreadyLiked(filmId, userId);
        filmRepository.addNewLike(userId, filmId);
        return Map.of("message", "лайк успешно добавлен");
    }

    public Map<String, String> deleteLike(Long filmId, Long userId) {
        checkFilmOrThrow(filmId);
        userService.checkUserOrThrow(userId);
        throwIfLikeDoesNotExist(filmId, userId);
        filmRepository.deleteLikeFromFilm(userId, filmId);
        return Map.of("message", "лайк успешно удален");
    }

    public Collection<FilmDto> getMostFavoriteFilms(Long count) {
        return filmRepository.findMostPopularFilms(count);
    }

    private void throwIfAlreadyLiked(Long filmId, Long userId) {
        if (filmRepository.hasLike(userId, filmId)) {
            throw new DuplicatedDataException(String.format("Пользователь с id: %d уже поставил лайк фильму с id: %d", userId, filmId));
        }
    }

    private void throwIfLikeDoesNotExist(Long filmId, Long userId) {
        if (!filmRepository.hasLike(userId, filmId)) {
            throw new NotFoundDataException(String.format("У пользователь с id: %d нет лайка к фильму с id: %d",
                    userId, filmId));
        }
    }

    private void checkFilmOrThrow(Long id) {
        if (filmRepository.findById(id).isEmpty()) {
            log.warn("Отправлен не существующий id:{} c фильмом", id);
            throw new NotFoundDataException("Отправлен несуществующий id: " + id);
        }
    }
}
