package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.Film;

import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    List<Film> findAll();

    Film findById(Long id);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Film> getPopularFilms(int count);
}
