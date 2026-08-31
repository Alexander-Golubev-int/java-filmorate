package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.GenreDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.dal.dto.UpdateFilmRequestDto;
import ru.yandex.practicum.filmorate.storage.service.FilmService;
import ru.yandex.practicum.filmorate.validator.Create;
import ru.yandex.practicum.filmorate.validator.Update;

import java.util.Collection;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping(value = "/genre")
    public Collection<GenreDto> getGenre() {
        return filmService.getGenre();
    }

    @GetMapping(value = "/genre/{id}")
    public GenreDto getGenre(@PathVariable @Positive(message = "ID должен быть > 0") Long id) {
        return filmService.getGenreId(id);
    }

    @GetMapping(value = "/popular")
    public Collection<FilmDto> getPopularsFilms(@RequestParam(defaultValue = "10") Long count) {
        return filmService.getMostFavoriteFilms(count);
    }

    @PostMapping
    public FilmDto createFilm(@Validated(Create.class) @RequestBody NewFilmRequest film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public FilmDto updateFilm(@Validated(Update.class) @RequestBody UpdateFilmRequestDto film) {
        return filmService.updateFilm(film);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public Map<String, String> addLike(@PathVariable @Positive(message = "ID должен быть > 0") Long id,
                                       @PathVariable @Positive(message = "UserId должен быть > 0") Long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public Map<String, String> deleteLike(@PathVariable @Positive(message = "ID должен быть > 0") Long id,
                                          @PathVariable @Positive(message = "UserId должен быть > 0") Long userId) {
        return filmService.deleteLike(id, userId);
    }
}
