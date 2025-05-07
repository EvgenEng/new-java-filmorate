package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film) throws NotFoundException;

    List<Film> findAll();

    Film findById(Long id) throws NotFoundException;
}
