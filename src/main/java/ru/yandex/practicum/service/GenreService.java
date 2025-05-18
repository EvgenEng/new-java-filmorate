package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.GenreDto;

import java.util.List;

public interface GenreService {

    GenreDto getGenreDto(Integer genreId);

    boolean existsById(Integer id);

    List<GenreDto> getAllGenres();
}
