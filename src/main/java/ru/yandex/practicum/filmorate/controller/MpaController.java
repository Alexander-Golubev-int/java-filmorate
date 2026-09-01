package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.storage.dal.dto.AgeRatingDto;
import ru.yandex.practicum.filmorate.storage.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final FilmService filmService;

    @GetMapping
    public Collection<AgeRatingDto> getAll() {
        return filmService.getMpa();
    }

    @GetMapping("/{id}")
    public AgeRatingDto getById(@PathVariable @Positive Long id) {
        return filmService.getMpaById(id);
    }
}