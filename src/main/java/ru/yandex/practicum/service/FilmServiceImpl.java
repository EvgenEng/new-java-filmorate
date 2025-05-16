package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaService mpaService;
    private final GenreService genreService;

    @Override
    public Film addFilm(Film film) {
        Film createdFilm = filmStorage.create(film);
        log.info("Film {} added", createdFilm.getId());
        return createdFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        Film updatedFilm = filmStorage.update(film);
        log.info("Film {} updated", updatedFilm.getId());
        return updatedFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    @Override
    public Film getFilmById(Long id) throws NotFoundException {
        return filmStorage.findById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return filmStorage.existsById(id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        filmStorage.addLike(filmId, userId);
        log.info("User {} liked film {}", userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        filmStorage.removeLike(filmId, userId);
        log.info("User {} removed like from film {}", userId, filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    @Override
    public Film validateAndCreateFilm(Film film) {
        validateFilmData(film);
        return addFilm(film);
    }

    @Override
    public Film validateAndUpdateFilm(Film film) {
        validateFilmData(film);
        if (!existsById(film.getId())) {
            throw new NotFoundException("Film with ID " + film.getId() + " not found");
        }
        return updateFilm(film);
    }

    @Override
    public void validateFilmData(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getMpaId() == null) {
            throw new ValidationException("MpaId cannot be null");
        }
        if (!mpaService.existsById(film.getMpaId())) {
            throw new NotFoundException("MPA rating with id " + film.getMpaId() + " not found");
        }

        if (film.getGenreIds() != null) {
            for (Integer genreId : film.getGenreIds()) {
                if (!genreService.existsById(genreId)) {
                    throw new NotFoundException("Genre with id " + genreId + " not found");
                }
            }
        }
    }
}
