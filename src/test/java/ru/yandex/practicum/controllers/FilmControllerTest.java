package ru.yandex.practicum.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.model.Film;

import java.time.LocalDate;

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
        ResponseEntity<Film> response = restTemplate.postForEntity("/films", testFilm, Film.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Test Film", response.getBody().getName());
    }

    @Test
    void shouldGetAllFilms() {
        restTemplate.postForEntity("/films", testFilm, Film.class);

        ResponseEntity<Film[]> response = restTemplate.getForEntity("/films", Film[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void shouldUpdateFilm() {
        Film created = restTemplate.postForEntity("/films", testFilm, Film.class).getBody();
        assertNotNull(created);
        created.setName("Updated Film");

        restTemplate.put("/films", created);
        ResponseEntity<Film[]> response = restTemplate.getForEntity("/films", Film[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length > 0);
        assertEquals("Test Film", response.getBody()[0].getName());
    }

    @Test
    void shouldNotUpdateFilmWithEmptyName() {
        Film created = restTemplate.postForEntity("/films", testFilm, Film.class).getBody();
        assertNotNull(created);
        created.setName(""); // Устанавливаем пустое имя

        ResponseEntity<Film> response = restTemplate.postForEntity("/films", created, Film.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldNotUpdateFilmWithEarlyReleaseDate() {
        Film created = restTemplate.postForEntity("/films", testFilm, Film.class).getBody();
        assertNotNull(created);
        created.setReleaseDate(LocalDate.of(1895, 12, 27)); // Устанавливаем раннюю дату релиза

        ResponseEntity<Film> response = restTemplate.postForEntity("/films", created, Film.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldNotUpdateFilmWithNegativeDuration() {
        Film created = restTemplate.postForEntity("/films", testFilm, Film.class).getBody();
        assertNotNull(created);
        created.setDuration(-10); // Устанавливаем отрицательную продолжительность

        ResponseEntity<Film> response = restTemplate.postForEntity("/films", created, Film.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldNotCreateFilmWithLongDescription() {
        testFilm.setDescription("A".repeat(201));

        ResponseEntity<Film> response = restTemplate.postForEntity("/films", testFilm, Film.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundForUnknownFilmUpdate() {
        Film unknownFilm = new Film();
        unknownFilm.setId(999L);
        unknownFilm.setName("Unknown Film");
        unknownFilm.setDescription("Description");
        unknownFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        unknownFilm.setDuration(120);

        ResponseEntity<Void> response = restTemplate.exchange("/films", HttpMethod.PUT,
                new HttpEntity<>(unknownFilm), Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
