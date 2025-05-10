package ru.yandex.practicum.service;

import ru.yandex.practicum.model.GenreDto;

public interface GenreService {

    GenreDto getGenreDto(Integer genreId);

    boolean existsById(Integer id);
}
