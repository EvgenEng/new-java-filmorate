package ru.yandex.practicum.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.exception.ValidationException;
import jakarta.validation.Valid;
import java.util.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/films")
@Slf4j

public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private long idCounter = 1;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Добавлено для правильного статуса
    public Film create(@Valid @RequestBody Film film) {
        validateFilm(film);
        film.setId(idCounter++);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            String errorMessage = "Фильм с id=" + film.getId() + " не найден";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Обновлен фильм: {}", film);
        return film;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        // Дополнительные проверки (по желанию)
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}

