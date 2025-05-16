package ru.yandex.practicum.service;

import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Film;
import java.util.List;

public interface FilmService {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(Long id) throws NotFoundException;

    void addLike(Long filmId, Long userId) throws NotFoundException;

    void removeLike(Long filmId, Long userId) throws NotFoundException;

    List<Film> getPopularFilms(int count);

    boolean existsById(Long id);

    Film validateAndCreateFilm(Film film);

    Film validateAndUpdateFilm(Film film);

    void validateFilmData(Film film);
}
