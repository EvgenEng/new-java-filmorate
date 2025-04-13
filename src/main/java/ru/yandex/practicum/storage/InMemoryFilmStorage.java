package ru.yandex.practicum.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> likes = new HashMap<>();
    private final UserStorage userStorage;
    private long idCounter = 1;

    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Film create(Film film) {
        film.setId(idCounter++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Film not found");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(Long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Film not found");
        }
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Film with ID " + filmId + " not found");
        }
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }
        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Film with ID " + filmId + " not found");
        }
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }
        Set<Long> filmLikes = likes.get(filmId);
        if (filmLikes != null) {
            filmLikes.remove(userId);
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> {
                    int likes1 = likes.getOrDefault(f1.getId(), Collections.emptySet()).size();
                    int likes2 = likes.getOrDefault(f2.getId(), Collections.emptySet()).size();
                    return Integer.compare(likes2, likes1); // Сортировка по убыванию
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}
