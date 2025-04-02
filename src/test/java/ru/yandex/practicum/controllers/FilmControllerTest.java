package ru.yandex.practicum.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.model.Film;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);
    }

    @Test
    void shouldCreateFilm() {
        ResponseEntity<Film> response = restTemplate.postForEntity(
                "/films",
                testFilm,
                Film.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode()); // Изменено на OK
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Test Film", response.getBody().getName());
    }

    @Test
    void shouldGetAllFilms() {
        restTemplate.postForEntity("/films", testFilm, Film.class);

        ResponseEntity<Film[]> response = restTemplate.getForEntity(
                "/films",
                Film[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).length > 0);
    }

    @Test
    void shouldNotCreateFilmWithEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        ResponseEntity<String> response = restTemplate.postForEntity("/films", film, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldNotCreateFilmWithInvalidReleaseDate() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/films",
                film,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Дата релиза не может быть раньше 28 декабря 1895 года"));
    }

    @Test
    void shouldNotCreateFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-10);

        ResponseEntity<String> response = restTemplate.postForEntity("/films", film, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldNotCreateFilmWithTooLongDescription() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("A".repeat(201)); // Описание длиннее 200 символов
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        ResponseEntity<String> response = restTemplate.postForEntity("/films", film, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
