package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.storage.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmStorage.removeLike(filmId, userId);
    }

    public Film getFilmById(Long id) {
        return filmStorage.findById(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.findAll();
    }
}
