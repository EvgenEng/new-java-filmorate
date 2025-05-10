package ru.yandex.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.FilmGenre;
import ru.yandex.practicum.model.GenreDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    public List<GenreDto> getAllGenres() {
        return List.of(FilmGenre.values()).stream()
                .map(g -> new GenreDto(g.ordinal() + 1, g.getRussianName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public GenreDto getGenre(@PathVariable int id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
                    FilmGenre filmGenre = FilmGenre.values()[rs.getInt("genre_id") - 1];
                    return new GenreDto(filmGenre.ordinal() + 1, filmGenre.getRussianName());
                }, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Genre not found"));
    }
}
