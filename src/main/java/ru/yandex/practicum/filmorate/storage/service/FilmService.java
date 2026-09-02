package ru.yandex.practicum.filmorate.storage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundDataException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.storage.dal.dto.*;
import ru.yandex.practicum.filmorate.storage.dal.repository.FilmRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserService userService;

    public Collection<FilmDto> getFilms() {
        return filmRepository.findAll();
    }

    public Collection<GenreDto> getGenre() {
        return filmRepository.findAllGenre();
    }

    public Collection<AgeRatingDto> getMpa() {
        return filmRepository.findAllMpa();
    }

    public AgeRatingDto getMpaById(Long id) {
        return filmRepository.findMpaById(id);
    }

    public FilmDto getFilmById(Long id) {
        return filmRepository.findById(id);
    }

    public GenreDto getGenreId(Long id) {
        return filmRepository.findGenreById(id);
    }

    public FilmDto createFilm(NewFilmRequest film) {
        filmRepository.findMpaById(film.getMpa().getId());
        if (film.getGenres() != null) {
            for (GenreDto genre : film.getGenres()) {
                if (genre.getId() == null) {
                    throw new NotFoundDataException("Id жанра не может быть null");
                }
                filmRepository.findGenreById(genre.getId());
            }
        }
        if (film.getGenres() != null) {
            List<GenreDto> uniqueGenres = film.getGenres().stream()
                    .filter(g -> g.getId() != null)
                    .collect(Collectors.toMap(
                            GenreDto::getId,
                            g -> g,
                            (existing, replacement) -> existing,  // оставляем первый
                            LinkedHashMap::new
                    ))
                    .values()
                    .stream()
                    .toList();

            film.setGenres(uniqueGenres);
        }
        return filmRepository.addNewFilm(film);
    }

    public FilmDto updateFilm(UpdateFilmRequestDto film) {
        try {
            checkFilmOrThrow(film.getId());
        } catch (NullPointerException e) {
            throw new ValidationException("Отправлен некорректный JSON");
        }
        return filmRepository.updateFilm(film);
    }

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

    public Map<String, String> deleteGenreFromFilm(Long filmId, Long genreId) {
        checkFilmOrThrow(filmId);
        filmRepository.deleteGenreFromFilm(filmId, genreId);
        return Map.of("message", "жанр удален");
    }

    private void throwIfLikeDoesNotExist(Long filmId, Long userId) {
        if (!filmRepository.hasLike(userId, filmId)) {
            throw new NotFoundDataException(String.format("У пользователь с id: %d нет лайка к фильму с id: %d",
                    userId, filmId));
        }
    }

    private void checkFilmOrThrow(Long id) {
        filmRepository.findById(id);
    }
}
