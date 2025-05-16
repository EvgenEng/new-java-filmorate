package ru.yandex.practicum.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Film;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Override
    public Film create(Film film) {

        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpaId());
            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        // Сохраняем жанры, если они есть
        if (film.getGenreIds() != null && !film.getGenreIds().isEmpty()) {
            updateFilmGenres(film);
        }

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
                film.getMpaId(),
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

    @Override
    public boolean existsById(Long filmId) {
        String sql = "SELECT COUNT(*) FROM films WHERE film_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId);
        return count != null && count > 0;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        if (!existsById(filmId)) {
            throw new NotFoundException("Film not found");
        }
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        int deleted = jdbcTemplate.update(sql, filmId, userId);
        if (deleted == 0) {
            throw new NotFoundException("Like not found");
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = """
            SELECT f.*, COUNT(fl.user_id) AS likes_count
            FROM films f
            LEFT JOIN film_likes fl ON f.film_id = fl.film_id
            GROUP BY f.film_id
            ORDER BY likes_count DESC
            LIMIT ?
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = mapRowToFilm(rs, rowNum);
            film.setLikesCount(rs.getInt("likes_count"));
            return film;
        }, count);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpaId(rs.getInt("mpa_rating_id"));

        // Загрузка жанров фильма
        String genresSql = "SELECT g.genre_id " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?";

        Set<Integer> genreIds = new HashSet<>(jdbcTemplate.query(genresSql,
                (rsg, rowNumg) -> rsg.getInt("genre_id"),
                film.getId()));

        film.setGenreIds(genreIds);

        return film;
    }

    private void updateFilmGenres(Film film) {
        // Удаляем старые жанры
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        // Добавляем новые жанры
        if (film.getGenreIds() != null && !film.getGenreIds().isEmpty()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

            List<Object[]> batchArgs = film.getGenreIds().stream()
                    .map(genreId -> new Object[]{film.getId(), genreId})
                    .collect(Collectors.toList());

            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }
}
