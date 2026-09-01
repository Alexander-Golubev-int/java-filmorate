package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.storage.dal.dto.GenreDto;
import ru.yandex.practicum.filmorate.storage.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final FilmService filmService;

    @GetMapping
    public Collection<GenreDto> getAll() {
        return filmService.getGenre();
    }

    @GetMapping("/{id}")
    public GenreDto getById(@PathVariable @Positive Long id) {
        return filmService.getGenreId(id);
    }
}
