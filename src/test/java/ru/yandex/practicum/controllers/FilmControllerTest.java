package ru.yandex.practicum.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import ru.yandex.practicum.dto.MpaDto;
import ru.yandex.practicum.exception.ErrorResponse;
import ru.yandex.practicum.model.User;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private FilmController.FilmRequest testFilmRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        testFilmRequest = new FilmController.FilmRequest();
        testFilmRequest.setName("Test Film");
        testFilmRequest.setDescription("Test Description");
        testFilmRequest.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilmRequest.setDuration(120);
        testFilmRequest.setMpa(new MpaDto(1, "G", "General Audiences"));
        testFilmRequest.setGenres(new HashSet<>());

        testUser = new User();
        testUser.setName("Test User");
        testUser.setLogin("testlogin");
        testUser.setEmail("test@example.com");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void shouldCreateFilm() {
        ResponseEntity<FilmController.FilmResponse> response = restTemplate.postForEntity(
                "/films",
                testFilmRequest,
                FilmController.FilmResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        FilmController.FilmResponse createdFilm = response.getBody();
        assertNotNull(createdFilm);
        assertEquals("Test Film", createdFilm.getName());
        assertNotNull(createdFilm.getMpa());
        assertEquals(1, createdFilm.getMpa().getId());
    }

    @Test
    void shouldGetAllFilms() {
        restTemplate.postForEntity("/films", testFilmRequest, FilmController.FilmResponse.class);

        ResponseEntity<FilmController.FilmResponse[]> response = restTemplate.getForEntity(
                "/films",
                FilmController.FilmResponse[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void shouldGetFilmById() {
        FilmController.FilmResponse createdFilm = restTemplate.postForEntity(
                "/films",
                testFilmRequest,
                FilmController.FilmResponse.class
        ).getBody();

        assertNotNull(createdFilm);

        ResponseEntity<FilmController.FilmResponse> response = restTemplate.getForEntity(
                "/films/" + createdFilm.getId(),
                FilmController.FilmResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdFilm.getId(), response.getBody().getId());
    }

    @Test
    void shouldUpdateFilm() {
        FilmController.FilmResponse createdFilm = restTemplate.postForEntity(
                "/films",
                testFilmRequest,
                FilmController.FilmResponse.class
        ).getBody();

        assertNotNull(createdFilm);

        testFilmRequest.setId(createdFilm.getId());
        testFilmRequest.setName("Updated Film");
        testFilmRequest.setMpa(new MpaDto(2, "PG", "Parental Guidance Suggested"));

        ResponseEntity<FilmController.FilmResponse> response = restTemplate.exchange(
                "/films/" + createdFilm.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(testFilmRequest),
                FilmController.FilmResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Film", response.getBody().getName());
        assertEquals(2, response.getBody().getMpa().getId());
    }

    @Test
    void shouldNotCreateFilmWithInvalidData() {
        testFilmRequest.setName("");

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/films",
                testFilmRequest,
                ErrorResponse.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrors());
    }

    @Test
    void shouldAddAndRemoveLike() {
        // Create user
        User createdUser = restTemplate.postForEntity("/users", testUser, User.class).getBody();
        assertNotNull(createdUser);

        // Create film
        FilmController.FilmResponse createdFilm = restTemplate.postForEntity(
                "/films",
                testFilmRequest,
                FilmController.FilmResponse.class
        ).getBody();
        assertNotNull(createdFilm);

        // Add like
        ResponseEntity<Void> addLikeResponse = restTemplate.exchange(
                "/films/" + createdFilm.getId() + "/like/" + createdUser.getId(),
                HttpMethod.PUT,
                null,
                Void.class
        );
        assertEquals(HttpStatus.OK, addLikeResponse.getStatusCode());

        // Remove like
        ResponseEntity<Void> removeLikeResponse = restTemplate.exchange(
                "/films/" + createdFilm.getId() + "/like/" + createdUser.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.OK, removeLikeResponse.getStatusCode());
    }

    @Test
    void shouldGetPopularFilms() {
        // Create two films
        FilmController.FilmResponse film1 = restTemplate.postForEntity(
                "/films",
                testFilmRequest,
                FilmController.FilmResponse.class
        ).getBody();
        assertNotNull(film1);

        testFilmRequest.setName("Another Film");
        FilmController.FilmResponse film2 = restTemplate.postForEntity(
                "/films",
                testFilmRequest,
                FilmController.FilmResponse.class
        ).getBody();
        assertNotNull(film2);

        // Create user
        User user = restTemplate.postForEntity("/users", testUser, User.class).getBody();
        assertNotNull(user);

        // Add like to first film
        restTemplate.exchange(
                "/films/" + film1.getId() + "/like/" + user.getId(),
                HttpMethod.PUT,
                null,
                Void.class
        );

        // Get popular films
        ResponseEntity<FilmController.FilmResponse[]> response = restTemplate.getForEntity(
                "/films/popular?count=2",
                FilmController.FilmResponse[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
    }
}
