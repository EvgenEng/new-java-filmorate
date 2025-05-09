package ru.yandex.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.MpaRating;
import ru.yandex.practicum.model.MpaDto;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    public List<MpaDto> getAllMpa() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY mpa_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            MpaRating mpaRating = MpaRating.values()[rs.getInt("mpa_id") - 1];
            return new MpaDto(mpaRating.ordinal() + 1, mpaRating.name());
        });
    }

    @GetMapping("/{id}")
    public MpaDto getMpa(@PathVariable int id) {
        String sql = "SELECT * FROM mpa_ratings WHERE mpa_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
                    MpaRating mpaRating = MpaRating.values()[rs.getInt("mpa_id") - 1];
                    return new MpaDto(mpaRating.ordinal() + 1, mpaRating.name());
                }, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("MPA rating not found"));
    }
}
