package ru.yandex.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.FilmGenre;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    public List<FilmGenre> getAllGenres() {
        String sql = "SELECT * FROM genres ORDER BY genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                FilmGenre.values()[rs.getInt("genre_id") - 1]);
    }

    @GetMapping("/{id}")
    public FilmGenre getGenreById(@PathVariable int id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                        FilmGenre.values()[rs.getInt("genre_id") - 1], id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Genre not found"));
    }
}
