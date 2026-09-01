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
    import ru.yandex.practicum.filmorate.storage.dal.dto.*;
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

        @GetMapping(value = "/popular")
        public Collection<FilmDto> getPopularsFilms(@RequestParam(defaultValue = "10") Long count) {
            return filmService.getMostFavoriteFilms(count);
        }

        @GetMapping(value = "/{id}")
        public FilmDto getFilmsById(@PathVariable @Positive(message = "ID должен быть > 0") Long id) {
            return filmService.getFilmById(id);
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

        @DeleteMapping(value = "/{filmId}/genre/{genreId}")
        public Map<String, String> deleteGenreFromFilm(@PathVariable @Positive(message = "ID должен быть > 0") Long filmId,
                                              @PathVariable @Positive(message = "genreId должен быть от 1 до 5") Long genreId) {
            return filmService.deleteGenreFromFilm(filmId, genreId);
        }
    }
