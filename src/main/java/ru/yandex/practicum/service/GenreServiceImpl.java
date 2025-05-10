package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.FilmGenre;
import ru.yandex.practicum.model.GenreDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    @Override
    public GenreDto getGenreDto(Integer genreId) {
        try {
            FilmGenre genre = FilmGenre.values()[genreId - 1];
            return new GenreDto(genreId, genre.getRussianName());
        } catch (ArrayIndexOutOfBoundsException e) {
            log.warn("Unknown genre ID: {}", genreId);
            throw new NotFoundException("Genre with id " + genreId + " not found");
        }
    }

    @Override
    public boolean existsById(Integer id) {
        try {
            getGenreDto(id);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }
}
