package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> getFilms();

    Film createFilm(@Valid @RequestBody Film film);

    Film updateFilm(@Valid @RequestBody Film film);
}
