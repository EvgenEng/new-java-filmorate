package ru.yandex.practicum.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.storage.FilmDbStorage;
import ru.yandex.practicum.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, FilmDbStorage.class})
class DbStorageTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    void testCreateAndFindUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.create(user);
        assertNotNull(createdUser.getId());

        User foundUser = userStorage.findById(createdUser.getId());
        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals("test@example.com", foundUser.getEmail());
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser = userStorage.create(user);

        createdUser.setName("Updated Name");
        User updatedUser = userStorage.update(createdUser);

        assertEquals("Updated Name", updatedUser.getName());
    }

    @Test
    void testCreateAndFindFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpaId(2); // PG имеет id = 2
        film.setGenreIds(Set.of(1)); // COMEDY имеет id = 1

        Film createdFilm = filmStorage.create(film);
        assertNotNull(createdFilm.getId());

        Film foundFilm = filmStorage.findById(createdFilm.getId());
        assertEquals(createdFilm.getId(), foundFilm.getId());
        assertEquals("Test Film", foundFilm.getName());
        assertEquals(1, foundFilm.getGenreIds().size()); // Проверяем количество жанров
        assertTrue(foundFilm.getGenreIds().contains(1)); // Проверяем наличие жанра COMEDY
    }

    @Test
    void testUpdateFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpaId(1); // G имеет id = 1
        film.setGenreIds(Set.of(2)); // DRAMA имеет id = 2

        Film createdFilm = filmStorage.create(film);

        createdFilm.setName("Updated Film");
        createdFilm.setMpaId(3); // PG-13 имеет id = 3
        createdFilm.setGenreIds(Set.of(1, 3)); // COMEDY (1) и ANIMATION (3)

        Film updatedFilm = filmStorage.update(createdFilm);

        assertEquals("Updated Film", updatedFilm.getName());
        assertEquals(3, updatedFilm.getMpaId());
        assertEquals(2, updatedFilm.getGenreIds().size());
        assertTrue(updatedFilm.getGenreIds().containsAll(Set.of(1, 3)));
    }
}
