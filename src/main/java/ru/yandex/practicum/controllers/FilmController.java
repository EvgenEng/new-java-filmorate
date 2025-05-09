package ru.yandex.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.exception.ErrorResponse;
import ru.yandex.practicum.service.FilmService;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film filmRequest) {
        Film createdFilm = filmService.addFilm(filmRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdFilm);
    }

    @PutMapping("/{id}")
    public Film update(@PathVariable Long id, @Valid @RequestBody Film film) {
        film.setId(id); // Устанавливаем ID для обновления
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        "Validation error",
                        "Invalid request parameters",
                        HttpStatus.BAD_REQUEST,
                        LocalDateTime.now(),
                        errors
                ));
    }
}
