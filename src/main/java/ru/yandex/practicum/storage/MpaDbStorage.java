package ru.yandex.practicum.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.MpaDto;
import ru.yandex.practicum.model.MpaRating;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<MpaDto> getMpaById(Integer id) {
        if (id < 1 || id > MpaRating.values().length) {
            return Optional.empty();
        }
        MpaRating rating = MpaRating.values()[id - 1];
        return Optional.of(new MpaDto(rating.ordinal() + 1, rating.getName()));
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM mpa_ratings WHERE mpa_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
