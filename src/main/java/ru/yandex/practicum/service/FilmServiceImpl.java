package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private final Map<Long, Set<Long>> likes = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        Film createdFilm = filmStorage.create(film);
        log.info("Film {} added", createdFilm.getId());
        return createdFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        Film updatedFilm = filmStorage.update(film);
        log.info("Film {} updated", updatedFilm.getId());
        return updatedFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    @Override
    public Film getFilmById(Long id) throws NotFoundException {
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new NotFoundException("Film with ID " + id + " not found");
        }
        return film;
    }

    @Override
    public boolean existsById(Long id) {
        return filmStorage.findById(id) != null;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        if (!filmStorage.existsById(filmId)) {
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
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpaId(rs.getInt("mpa_rating_id"));

        // Если нужны жанры:
        Set<Integer> genreIds = new HashSet<>();
        String genresSql = "SELECT genre_id FROM film_genres WHERE film_id = ?";
        jdbcTemplate.query(genresSql,
                (rsGenres, row) -> genreIds.add(rsGenres.getInt("genre_id")),
                film.getId());
        film.setGenreIds(genreIds);

        return film;
    }

    private void validateFilmAndUserExist(Long filmId, Long userId) throws NotFoundException {
        if (!existsById(filmId)) {
            log.warn("Film with ID {} not found when processing like", filmId);
            throw new NotFoundException("Film with ID " + filmId + " not found");
        }

        if (!userStorage.existsById(userId)) {
            log.warn("User with ID {} not found when processing like", userId);
            throw new NotFoundException("User with ID " + userId + " not found");
        }
    }
}
