package ru.yandex.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.MpaRating;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    public List<MpaRating> getAllMpaRatings() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY mpa_id";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                MpaRating.values()[rs.getInt("mpa_id") - 1]);
    }

    @GetMapping("/{id}")
    public MpaRating getMpaRatingById(@PathVariable int id) {
        String sql = "SELECT * FROM mpa_ratings WHERE mpa_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                        MpaRating.values()[rs.getInt("mpa_id") - 1], id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("MPA rating not found"));
    }
}
