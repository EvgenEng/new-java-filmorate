package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.GenreDto;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.storage.GenreStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;

    @Override
    public GenreDto getGenreDto(Integer genreId) {
        return genreStorage.findById(genreId)
                .map(this::convertToDto)
                .orElseThrow(() -> {
                    log.warn("Unknown genre ID: {}", genreId);
                    return new NotFoundException("Genre with id " + genreId + " not found");
                });
    }

    @Override
    public boolean existsById(Integer id) {
        return genreStorage.existsById(id);
    }

    private GenreDto convertToDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getRussianName());
    }

    @Override
    public List<GenreDto> getAllGenres() {
        return genreStorage.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
