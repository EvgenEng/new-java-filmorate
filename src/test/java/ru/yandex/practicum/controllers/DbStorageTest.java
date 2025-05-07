package ru.yandex.practicum.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.FilmGenre;
import ru.yandex.practicum.model.MpaRating;
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
        film.setMpa(MpaRating.PG);
        film.setGenres(Set.of(FilmGenre.COMEDY));

        Film createdFilm = filmStorage.create(film);
        assertNotNull(createdFilm.getId());

        Film foundFilm = filmStorage.findById(createdFilm.getId());
        assertEquals(createdFilm.getId(), foundFilm.getId());
        assertEquals("Test Film", foundFilm.getName());
        assertEquals(1, foundFilm.getGenres().size());
    }
}
