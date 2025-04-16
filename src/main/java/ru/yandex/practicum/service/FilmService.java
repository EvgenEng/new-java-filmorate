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
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> likes = new HashMap<>();

    public Film addFilm(Film film) {
        Film createdFilm = filmStorage.create(film);
        log.info("Film {} added", createdFilm.getId());
        return createdFilm;
    }

    public Film updateFilm(Film film) {
        Film updatedFilm = filmStorage.update(film);
        log.info("Film {} updated", updatedFilm.getId());
        return updatedFilm;
    }

    public List<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    public Film getFilmById(Long id) {
        return filmStorage.findById(id);
    }

    public void addLike(Long filmId, Long userId) {
        if (!userStorage.existsById(userId)) {
            log.warn("User with ID {} tried to like film {} but does not exist", userId, filmId);
            throw new NotFoundException("User with ID " + userId + " not found");
        }
        filmStorage.findById(filmId); // Проверка существования фильма
        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        log.info("Film {} liked by user {}", filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (!userStorage.existsById(userId)) {
            log.warn("User with ID {} tried to remove like from film {} but does not exist", userId, filmId);
            throw new NotFoundException("User with ID " + userId + " not found");
        }
        filmStorage.findById(filmId); // Проверка существования фильма
        Set<Long> filmLikes = likes.get(filmId);
        if (filmLikes != null) {
            filmLikes.remove(userId);
            log.info("Like removed from film {} by user {}", filmId, userId);
        }
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Fetching top {} popular films", count);
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> {
                    int likes1 = likes.getOrDefault(f1.getId(), Collections.emptySet()).size();
                    int likes2 = likes.getOrDefault(f2.getId(), Collections.emptySet()).size();
                    return Integer.compare(likes2, likes1);
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}
