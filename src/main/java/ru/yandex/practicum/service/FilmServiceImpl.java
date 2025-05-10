package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
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
    public void addLike(Long filmId, Long userId) throws NotFoundException {
        validateFilmAndUserExist(filmId, userId);

        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        log.info("Film {} liked by user {}", filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) throws NotFoundException {
        validateFilmAndUserExist(filmId, userId);

        Set<Long> filmLikes = likes.get(filmId);
        if (filmLikes != null) {
            filmLikes.remove(userId);
            log.info("Like removed from film {} by user {}", filmId, userId);
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        log.info("Fetching top {} popular films", count);
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(
                        (Film film) -> likes.getOrDefault(film.getId(), Collections.emptySet()).size()
                ).reversed())
                .limit(count)
                .collect(Collectors.toList());
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
