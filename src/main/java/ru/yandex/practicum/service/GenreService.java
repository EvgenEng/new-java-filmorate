package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.FilmGenre;
import ru.yandex.practicum.model.GenreDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    public GenreDto getGenreDto(Integer genreId) {
        try {
            FilmGenre genre = FilmGenre.values()[genreId - 1];
            return new GenreDto(genreId, genre.getRussianName());
        } catch (ArrayIndexOutOfBoundsException e) {
            log.warn("Unknown genre ID: {}", genreId);
            return null;
        }
    }
}
