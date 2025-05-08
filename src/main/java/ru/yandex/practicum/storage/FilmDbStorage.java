package ru.yandex.practicum.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.FilmGenre;
import ru.yandex.practicum.model.MpaRating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        // Получаем ID из enum MpaRating
        int mpaId = film.getMpaRating().getId();

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, mpaId); // Используем правильный ID
            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? " +
                "WHERE film_id = ?";

        int updated = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpaRating().ordinal() + 1,
                film.getId());

        if (updated == 0) {
            throw new NotFoundException("Film not found");
        }

        updateFilmGenres(film);
        return film;
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.*, m.name AS mpa_name, m.description AS mpa_description " +
                "FROM films f " +
                "JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_id";

        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film findById(Long id) {
        String sql = "SELECT f.*, m.name AS mpa_name, m.description AS mpa_description " +
                "FROM films f " +
                "JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_id " +
                "WHERE f.film_id = ?";

        return jdbcTemplate.query(sql, this::mapRowToFilm, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Film not found"));
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        MpaRating mpa = MpaRating.values()[rs.getInt("mpa_rating_id") - 1];
        film.setMpaRating(mpa);

        // Загрузка жанров фильма
        String genresSql = "SELECT g.genre_id, g.name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?";

        List<FilmGenre> genres = jdbcTemplate.query(genresSql,
                (rsg, rowNumg) -> FilmGenre.values()[rsg.getInt("genre_id") - 1],
                film.getId());

        film.setGenres(new HashSet<>(genres));

        return film;
    }

    private void updateFilmGenres(Film film) {
        // Удаляем старые жанры
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        // Добавляем новые жанры
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

            List<Object[]> batchArgs = film.getGenres().stream()
                    .map(genre -> new Object[]{film.getId(), genre.ordinal() + 1})
                    .collect(Collectors.toList());

            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }
}
