package ru.yandex.practicum.controllers;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.exception.ErrorResponse;
import ru.yandex.practicum.service.FilmService;
import ru.yandex.practicum.service.MpaService;
import ru.yandex.practicum.service.GenreService;
import jakarta.validation.Valid;
import ru.yandex.practicum.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final UserService userService;

    /*@PostMapping
    public ResponseEntity<FilmResponse> createFilm(@Valid @RequestBody FilmRequest filmRequest) {
        log.info("Creating new film: {}", filmRequest.getName());
        Film film = convertRequestToFilm(filmRequest);
        validateMpa(film.getMpaId());
        validateGenres(film.getGenreIds());
        Film createdFilm = filmService.addFilm(film);
        log.info("Created film with ID: {}", createdFilm.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertToFilmResponse(createdFilm));
    }*/

    @PostMapping
    public ResponseEntity<FilmResponse> createFilm(@Valid @RequestBody FilmRequest filmRequest) {
        // Проверка существования MPA
        if (!mpaService.existsById(filmRequest.getMpa().getId())) {
            throw new NotFoundException("MPA rating with id " + filmRequest.getMpa().getId() + " not found");
        }

        // Проверка существования жанров
        if (filmRequest.getGenres() != null) {
            for (GenreDto genre : filmRequest.getGenres()) {
                if (!genreService.existsById(genre.getId())) {
                    throw new NotFoundException("Genre with id " + genre.getId() + " not found");
                }
            }
        }

        Film film = convertRequestToFilm(filmRequest);
        Film createdFilm = filmService.addFilm(film);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertToFilmResponse(createdFilm));
    }

    @PutMapping("/{id}")
    public FilmResponse update(@PathVariable Long id, @Valid @RequestBody FilmRequest filmRequest) {
        log.info("Updating film with ID: {}", id);
        Film film = convertRequestToFilm(filmRequest);
        film.setId(id);
        validateMpa(film.getMpaId());
        validateGenres(film.getGenreIds());
        Film updatedFilm = filmService.updateFilm(film);
        log.info("Film with ID {} updated successfully", id);
        return convertToFilmResponse(updatedFilm);
    }

    @GetMapping
    public ResponseEntity<List<FilmResponse>> getAllFilms() {
        log.info("Getting all films");
        List<FilmResponse> films = filmService.getAllFilms().stream()
                .map(this::convertToFilmResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(films);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmResponse> getFilm(@PathVariable Long id) {
        log.info("Getting film with ID: {}", id);
        Film film = filmService.getFilmById(id);
        return ResponseEntity.ok(convertToFilmResponse(film));
    }

    /*@PutMapping
    public ResponseEntity<FilmResponse> updateFilm(@Valid @RequestBody FilmRequest filmRequest) {
        log.info("Updating film");
        Film film = convertRequestToFilm(filmRequest);
        validateMpa(film.getMpaId());
        validateGenres(film.getGenreIds());
        Film updatedFilm = filmService.updateFilm(film);
        return ResponseEntity.ok(convertToFilmResponse(updatedFilm));
    }*/

    @PutMapping
    public ResponseEntity<FilmResponse> updateFilm(@Valid @RequestBody FilmRequest filmRequest) {
        // Проверка на null ID
        if (filmRequest.getId() == null) {
            throw new ValidationException("Film ID cannot be null for update");
        }

        log.info("Updating film with ID: {}", filmRequest.getId());
        Film film = convertRequestToFilm(filmRequest);

        // Явная проверка существования фильма
        if (!filmService.existsById(filmRequest.getId())) {
            throw new NotFoundException("Film with ID " + filmRequest.getId() + " not found");
        }

        Film updatedFilm = filmService.updateFilm(film);
        return ResponseEntity.ok(convertToFilmResponse(updatedFilm));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<FilmResponse>> getPopularFilms(
            @RequestParam(defaultValue = "10") @Min(1) int count) {
        log.info("Getting top {} popular films", count);
        List<FilmResponse> films = filmService.getPopularFilms(count).stream()
                .map(this::convertToFilmResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(films);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Void> addLike(
            @PathVariable Long filmId,
            @PathVariable Long userId) {

        log.info("Adding like from user {} to film {}", userId, filmId);

        try {
            filmService.addLike(filmId, userId);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            log.error("Error adding like: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Void> removeLike(
            @PathVariable Long filmId,
            @PathVariable Long userId) {

        log.info("Removing like from user {} to film {}", userId, filmId);

        try {
            filmService.removeLike(filmId, userId);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            log.error("Error removing like: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        log.error("Validation error: {}", errors);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        "Validation error",
                        "Invalid request parameters",
                        HttpStatus.BAD_REQUEST,
                        LocalDateTime.now(),
                        errors
                ));
    }

    /*private Film convertRequestToFilm(FilmRequest filmRequest) {
        Film film = new Film();
        film.setName(filmRequest.getName());
        film.setDescription(filmRequest.getDescription());
        film.setReleaseDate(filmRequest.getReleaseDate());
        film.setDuration(filmRequest.getDuration());

        // MPA
        if (filmRequest.getMpa() != null) {
            film.setMpaId(filmRequest.getMpa().getId());
        }

        // Жанры
        if (filmRequest.getGenres() != null) {
            film.setGenreIds(filmRequest.getGenres().stream()
                    .map(GenreDto::getId)
                    .collect(Collectors.toSet()));
        }

        return film;
    }*/

    private Film convertRequestToFilm(FilmRequest filmRequest) {
        Film film = new Film();
        film.setId(filmRequest.getId()); // Установка ID из запроса
        film.setName(filmRequest.getName());
        film.setDescription(filmRequest.getDescription());
        film.setReleaseDate(filmRequest.getReleaseDate());
        film.setDuration(filmRequest.getDuration());

        if (filmRequest.getMpa() != null) {
            film.setMpaId(filmRequest.getMpa().getId());
        }

        if (filmRequest.getGenres() != null) {
            film.setGenreIds(filmRequest.getGenres().stream()
                    .map(GenreDto::getId)
                    .collect(Collectors.toSet()));
        }

        return film;
    }

    private FilmResponse convertToFilmResponse(Film film) {
        FilmResponse response = new FilmResponse();
        response.setId(film.getId());
        response.setName(film.getName());
        response.setDescription(film.getDescription());
        response.setReleaseDate(film.getReleaseDate());
        response.setDuration(film.getDuration());

        if (film.getMpaId() != null) {
            response.setMpa(mpaService.getMpaById(film.getMpaId()));
        }

        if (film.getGenreIds() != null && !film.getGenreIds().isEmpty()) {
            response.setGenres(film.getGenreIds().stream()
                    .distinct()
                    .map(genreService::getGenreDto)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(GenreDto::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        }

        return response;
    }

    private void validateMpa(Integer mpaId) {
        if (mpaId == null) {
            log.error("Validation failed: MPA ID cannot be null");
            throw new ValidationException("MpaId cannot be null");
        }
        if (!mpaService.existsById(mpaId)) {
            log.error("MPA with ID {} not found", mpaId);
            throw new NotFoundException("MPA not found with id: " + mpaId);
        }
    }

    private void validateGenres(Set<Integer> genreIds) {
        if (genreIds != null) {
            for (Integer genreId : genreIds) {
                if (genreService.getGenreDto(genreId) == null) {
                    throw new NotFoundException("Genre not found with id: " + genreId);
                }
            }
        }
    }

    @Data
    public static class FilmRequest {
        @NotBlank(message = "Film name cannot be blank")
        private String name;

        @Size(max = 200, message = "Description must be less than 200 characters")
        private String description;

        @PastOrPresent(message = "Release date cannot be in the future")
        private LocalDate releaseDate;

        @Positive(message = "Duration must be positive")
        private int duration;

        @NotNull
        private MpaDto mpa;

        private Set<GenreDto> genres = new HashSet<>();
        @Getter
        private Long id;

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
}
