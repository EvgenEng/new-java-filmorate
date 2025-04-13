package ru.yandex.practicum.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> likes = new HashMap<>();
    private long idCounter = 1;

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
        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Set<Long> filmLikes = likes.get(filmId);
        if (filmLikes != null) {
            filmLikes.remove(userId);
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted(Comparator.comparingInt(film -> likes.getOrDefault(((Film) film).getId(),
                        Collections.emptySet()).size()).reversed())
                .limit(count)
                .toList();
    }
}
