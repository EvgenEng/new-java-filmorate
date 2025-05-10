package ru.yandex.practicum.controllers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.exception.ErrorResponse;
import ru.yandex.practicum.model.FilmGenre;
import ru.yandex.practicum.model.GenreDto;
import ru.yandex.practicum.model.MpaDto;
import ru.yandex.practicum.service.FilmService;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors; // Импортируем Collectors

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public ResponseEntity<FilmResponse> createFilm(@Valid @RequestBody FilmRequest filmRequest) {
        Film film = convertToFilm(filmRequest);
        Film createdFilm = filmService.addFilm(film);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertToFilmResponse(createdFilm));
    }

    @PutMapping("/{id}")
    public FilmResponse update(@PathVariable Long id, @Valid @RequestBody FilmRequest filmRequest) {
        Film film = convertToFilm(filmRequest);
        film.setId(id);
        Film updatedFilm = filmService.updateFilm(film);
        return convertToFilmResponse(updatedFilm);
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmResponse> getFilm(@PathVariable Long id) {
        Film film = filmService.getFilmById(id);
        return ResponseEntity.ok(convertToFilmResponse(film));
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(
            @PathVariable Long id,
            @PathVariable Long userId) {
        try {
            filmService.removeLike(id, userId);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
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

    @Data
    public static class FilmRequest {
        @NotBlank
        private String name;
        @Size(max = 200) private String description;
        private LocalDate releaseDate;
        @Positive
        private int duration;
        private Integer mpaId;
        private Set<Integer> genreIds;
    }

    @Data
    public static class FilmResponse {
        private Long id;
        private String name;
        private String description;
        private LocalDate releaseDate;
        private int duration;
        private MpaDto mpa;
        private Set<GenreDto> genres;
    }

    private Film convertToFilm(FilmRequest filmRequest) {
        Film film = new Film();
        film.setName(filmRequest.getName());
        film.setDescription(filmRequest.getDescription());
        film.setReleaseDate(filmRequest.getReleaseDate());
        film.setDuration(filmRequest.getDuration());
        film.setMpaId(filmRequest.getMpaId());
        film.setGenreIds(filmRequest.getGenreIds());
        return film;
    }

    private FilmResponse convertToFilmResponse(Film film) {
        FilmResponse response = new FilmResponse();
        response.setId(film.getId());
        response.setName(film.getName());
        response.setDescription(film.getDescription());
        response.setReleaseDate(film.getReleaseDate());
        response.setDuration(film.getDuration());

        // Исправляем получение MPA
        if (film.getMpaId() != null) {
            response.setMpa(new MpaDto(film.getMpaId(), getMpaName(film.getMpaId())));
        }

        // Исправляем получение жанров
        if (film.getGenres() != null) {
            response.setGenres(film.getGenres().stream()
                    .map(genre -> new GenreDto(genre.getId(), getGenreName(genre.getId())))
                    .collect(Collectors.toSet()));
        }

        return response;
    }

    private String getMpaName(Integer mpaId) {
        // Логика для получения названия MPA по ID
        return "MPA Name"; // Заменить на реальную логику
    }

    private String getGenreName(Integer genreId) {
        FilmGenre genre = FilmGenre.values()[genreId - 1];
        return genre.getRussianName();
    }
}
