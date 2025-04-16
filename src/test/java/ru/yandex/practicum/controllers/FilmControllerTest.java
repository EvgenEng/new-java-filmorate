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
import ru.yandex.practicum.exception.ErrorResponse;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.User;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private Film testFilm;
    private User testUser;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);

        testUser = new User();
        testUser.setName("Test User");
        testUser.setLogin("testlogin");
        testUser.setEmail("test@example.com");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
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
    void shouldTestFilm() {
        Film created = restTemplate.postForEntity("/films", testFilm, Film.class).getBody();
        assertNotNull(created);
        created.setName("Test Film");

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
        created.setName("");

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/films", created, ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldNotUpdateFilmWithEarlyReleaseDate() {
        Film created = restTemplate.postForEntity("/films", testFilm, Film.class).getBody();
        assertNotNull(created);
        created.setReleaseDate(LocalDate.of(1895, 12, 27));

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/films", created, ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldNotUpdateFilmWithNegativeDuration() {
        Film created = restTemplate.postForEntity("/films", testFilm, Film.class).getBody();
        assertNotNull(created);
        created.setDuration(-10);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/films", created, ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldNotCreateFilmWithLongDescription() {
        testFilm.setDescription("A".repeat(201));

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/films", testFilm, ErrorResponse.class);
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

    @Test
    void testAddLikeToUnknownFilm() {
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testuser");
        testUser.setBirthday(LocalDate.now().minusYears(20));

        ResponseEntity<User> userResponse = restTemplate.postForEntity("/users", testUser, User.class);
        assertEquals(HttpStatus.CREATED, userResponse.getStatusCode());
        User createdUser = userResponse.getBody();
        assertNotNull(createdUser);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/films/999/like/" + createdUser.getId(),
                HttpMethod.PUT,
                null,
                ErrorResponse.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Film not found"),
                "Actual error message: " + response.getBody().getMessage());
    }

    @Test
    void shouldAddLike() {
        User createdUser = restTemplate.postForEntity("/users", testUser, User.class).getBody();
        Film createdFilm = restTemplate.postForEntity("/films", testFilm, Film.class).getBody();
        assertNotNull(createdUser);
        assertNotNull(createdFilm);

        ResponseEntity<String> response = restTemplate.exchange(
                "/films/" + createdFilm.getId() + "/like/" + createdUser.getId(),
                HttpMethod.PUT,
                null,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldNotAddLikeFromUnknownUser() {
        Film createdFilm = restTemplate.postForEntity("/films", testFilm, Film.class).getBody();
        assertNotNull(createdFilm);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/films/" + createdFilm.getId() + "/like/999",
                HttpMethod.PUT,
                null,
                ErrorResponse.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("User with ID 999 not found"));
    }

    @Test
    void shouldRemoveLike() {
        User createdUser = restTemplate.postForEntity("/users", testUser, User.class).getBody();
        Film createdFilm = restTemplate.postForEntity("/films", testFilm, Film.class).getBody();
        assertNotNull(createdUser);
        assertNotNull(createdFilm);

        restTemplate.put("/films/" + createdFilm.getId() + "/like/" + createdUser.getId(), null);

        ResponseEntity<String> response = restTemplate.exchange(
                "/films/" + createdFilm.getId() + "/like/" + createdUser.getId(),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldNotRemoveLikeFromUnknownFilm() {
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testuser");
        testUser.setBirthday(LocalDate.now().minusYears(20));

        ResponseEntity<User> userResponse = restTemplate.postForEntity("/users", testUser, User.class);
        assertEquals(HttpStatus.CREATED, userResponse.getStatusCode());
        User createdUser = userResponse.getBody();
        assertNotNull(createdUser);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/films/999/like/" + createdUser.getId(),
                HttpMethod.DELETE,
                null,
                ErrorResponse.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Film not found"),
                "Actual error message: " + response.getBody().getMessage());
    }

    @Test
    void shouldNotRemoveLikeFromUnknownUser() {
        Film createdFilm = restTemplate.postForEntity("/films", testFilm, Film.class).getBody();
        assertNotNull(createdFilm);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/films/" + createdFilm.getId() + "/like/999",
                HttpMethod.DELETE,
                null,
                ErrorResponse.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("User with ID 999 not found"));
    }

    @Test
    void shouldGetPopularFilms() {
        Film film1 = createTestFilm("Film 1");
        Film film2 = createTestFilm("Film 2");
        Film film3 = createTestFilm("Film 3");

        User user1 = createTestUser("User 1");
        User user2 = createTestUser("User 2");

        restTemplate.put("/films/" + film1.getId() + "/like/" + user1.getId(), null);
        restTemplate.put("/films/" + film1.getId() + "/like/" + user2.getId(), null);
        restTemplate.put("/films/" + film2.getId() + "/like/" + user1.getId(), null);

        ResponseEntity<Film[]> response = restTemplate.getForEntity("/films/popular?count=2", Film[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).length);
        assertEquals("Film 1", response.getBody()[0].getName());
    }

    private Film createTestFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        return restTemplate.postForEntity("/films", film, Film.class).getBody();
    }

    private User createTestUser(String name) {
        User user = new User();
        user.setName(name);
        user.setLogin(name.toLowerCase().replace(" ", ""));
        user.setEmail(name.toLowerCase().replace(" ", "") + "@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return restTemplate.postForEntity("/users", user, User.class).getBody();
    }
}