package ru.yandex.practicum.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.exception.NotFoundException;
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
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        validateFilm(film);
        film.setId(idCounter++);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм с ID: {}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            log.error("Попытка обновления фильма без ID");
            throw new ValidationException("ID фильма не может быть null");
        }

        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с ID {} не найден", film.getId());
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }

        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Обновлен фильм с ID: {}", film.getId());
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        log.debug("Запрошен список всех фильмов, количество: {}", films.size());
        return new ArrayList<>(films.values());
    }

    private void validateFilm(Film film) {
        // Валидация даты релиза
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Некорректная дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        // Валидация названия
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Пустое название фильма");
            throw new ValidationException("Название фильма не может быть пустым");
        }

        // Валидация продолжительности
        if (film.getDuration() <= 0) {
            log.warn("Некорректная продолжительность фильма: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        // Валидация описания
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Слишком длинное описание фильма: {} символов", film.getDescription().length());
            throw new ValidationException("Описание фильма не может быть длиннее 200 символов");
        }
    }
}
