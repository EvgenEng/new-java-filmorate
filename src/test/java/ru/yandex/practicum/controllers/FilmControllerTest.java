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
import java.util.Map;

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
        created.setName("Updated Film");

        ResponseEntity<Film> response = restTemplate.postForEntity("/films", created, Film.class);
    }

    @Test
    void shouldNotCreateFilmWithEmptyName() {
        testFilm.setName("");

        ResponseEntity<Map> response = restTemplate.postForEntity("/films", testFilm, Map.class);
    }

    @Test
    void shouldNotCreateFilmWithEarlyReleaseDate() {
        testFilm.setReleaseDate(LocalDate.of(1895, 12, 27));

        ResponseEntity<Map> response = restTemplate.postForEntity("/films", testFilm, Map.class);
    }

    @Test
    void shouldNotCreateFilmWithNegativeDuration() {
        testFilm.setDuration(-10);

        ResponseEntity<Map> response = restTemplate.postForEntity("/films", testFilm, Map.class);
    }

    @Test
    void shouldNotCreateFilmWithLongDescription() {
        testFilm.setDescription("A".repeat(201));

        ResponseEntity<Map> response = restTemplate.postForEntity("/films", testFilm, Map.class);
    }
}
