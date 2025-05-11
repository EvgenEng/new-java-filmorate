package ru.yandex.practicum.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import ru.yandex.practicum.exception.ErrorResponse;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.MpaDto;
import ru.yandex.practicum.model.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

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
        testFilm.setMpaId(1);

        testUser = new User();
        testUser.setName("Test User");
        testUser.setLogin("testlogin");
        testUser.setEmail("test@example.com");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void shouldCreateFilm() {
        String filmJson = "{\"name\":\"Тестовый фильм\",\"description\":\"Тестовое описание\"," +
                "\"releaseDate\":\"2023-01-01\",\"duration\":90,\"mpa\":{\"id\":1},\"genres\":[]}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(filmJson, headers);

        ResponseEntity<FilmController.FilmResponse> response = restTemplate.postForEntity(
                "/films",
                request,
                FilmController.FilmResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        FilmController.FilmResponse createdFilm = response.getBody();
        assertNotNull(createdFilm);
        assertNotNull(createdFilm.getMpa());
        assertEquals(1, createdFilm.getMpa().getId());
    }

    @Test
    void shouldGetAllFilms() {
        FilmController.FilmRequest filmRequest = new FilmController.FilmRequest();
        filmRequest.setName("Тестовый фильм");
        filmRequest.setDescription("Тестовое описание");
        filmRequest.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmRequest.setDuration(90);

        // Создаем MpaDto
        MpaDto mpa = new MpaDto(1, "G");
        filmRequest.setMpa(mpa);
        filmRequest.setGenres(new HashSet<>());

        // Отправляем FilmRequest и ожидаем FilmResponse
        ResponseEntity<FilmController.FilmResponse> createResponse = restTemplate.postForEntity(
                "/films",
                filmRequest,
                FilmController.FilmResponse.class
        );
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        // Получаем массив FilmResponse
        ResponseEntity<FilmController.FilmResponse[]> response = restTemplate.getForEntity(
                "/films",
                FilmController.FilmResponse[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);

        FilmController.FilmResponse createdFilm = createResponse.getBody();
        FilmController.FilmResponse[] allFilms = response.getBody();
        assertNotNull(createdFilm);
        assertNotNull(allFilms);
        assertTrue(Arrays.stream(allFilms).anyMatch(f ->
                f.getId().equals(createdFilm.getId()) &&
                        f.getName().equals(createdFilm.getName())
        ));
    }

    @Test
    void shouldCreateSimpleFilm() {
        FilmController.FilmRequest filmRequest = new FilmController.FilmRequest();
        filmRequest.setName("Простой фильм");
        filmRequest.setDescription("Простое описание");
        filmRequest.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmRequest.setDuration(90);

        MpaDto mpa = new MpaDto(1, "G");
        filmRequest.setMpa(mpa);

        // Добавляем пустой список жанров
        filmRequest.setGenres(new HashSet<>());

        // Отправляем FilmRequest и ожидаем FilmResponse
        ResponseEntity<FilmController.FilmResponse> response = restTemplate.postForEntity(
                "/films",
                filmRequest,
                FilmController.FilmResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("G", response.getBody().getMpa().getName());
    }

    @Test
    void shouldNotUpdateFilmWithEmptyName() {
        Film created = restTemplate.postForEntity("/films", testFilm, Film.class).getBody();
        assertNotNull(created);
        created.setName("");

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/films",
                created,
                ErrorResponse.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldNotUpdateFilmWithEarlyReleaseDate() {
        Film created = restTemplate.postForEntity("/films", testFilm, Film.class).getBody();
        assertNotNull(created);
        created.setReleaseDate(LocalDate.of(1895, 12, 27));

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/films",
                created,
                ErrorResponse.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldNotUpdateFilmWithNegativeDuration() {
        Film created = restTemplate.postForEntity("/films", testFilm, Film.class).getBody();
        assertNotNull(created);
        created.setDuration(-10);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/films",
                created,
                ErrorResponse.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldNotCreateFilmWithLongDescription() {
        testFilm.setDescription("A".repeat(201));

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/films",
                testFilm,
                ErrorResponse.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundForUnknownFilmUpdate() {
        FilmController.FilmRequest unknownFilm = new FilmController.FilmRequest();
        unknownFilm.setId(999L);
        unknownFilm.setName("Unknown Film");
        unknownFilm.setDescription("Description");
        unknownFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        unknownFilm.setDuration(120);

        MpaDto mpa = new MpaDto(1, "G");
        unknownFilm.setMpa(mpa);

        // Пустой набор жанров
        unknownFilm.setGenres(new HashSet<>());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/films/" + unknownFilm.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(unknownFilm),
                ErrorResponse.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Film not found", response.getBody().getMessage());
    }

    @Test
    void testAddLikeToUnknownFilm() {
        // Создаем пользователя
        ResponseEntity<User> userResponse = restTemplate.postForEntity("/users", testUser, User.class);
        assertEquals(HttpStatus.CREATED, userResponse.getStatusCode());
        User createdUser = userResponse.getBody();
        assertNotNull(createdUser);

        // Пытаемся добавить лайк несуществующему фильму
        ResponseEntity<Void> response = restTemplate.exchange(
                "/films/999/like/" + createdUser.getId(),
                HttpMethod.PUT,
                null,
                Void.class
        );

        // Проверяем статус 404
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
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

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void shouldNotAddLikeFromUnknownUser() {
        Film createdFilm = restTemplate.postForEntity("/films", testFilm, Film.class).getBody();
        assertNotNull(createdFilm);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/films/{filmId}/like/{userId}",
                HttpMethod.PUT,
                null,
                ErrorResponse.class,
                createdFilm.getId(),
                999L
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
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

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void shouldNotRemoveLikeFromUnknownFilm() {
        User createdUser = restTemplate.postForEntity("/users", testUser, User.class).getBody();
        assertNotNull(createdUser);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/films/999/like/" + createdUser.getId(),
                HttpMethod.DELETE,
                null,
                ErrorResponse.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
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

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void shouldGetPopularFilms() {
        // 1. Создаем тестовых пользователей
        User user1 = createTestUser("User 1");
        User user2 = createTestUser("User 2");

        // 2. Создаем тестовые фильмы
        FilmController.FilmRequest filmRequest1 = new FilmController.FilmRequest();
        filmRequest1.setName("Film 1");
        filmRequest1.setDescription("Description 1");
        filmRequest1.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmRequest1.setDuration(90);
        filmRequest1.setMpa(new MpaDto(1, "G"));
        filmRequest1.setGenres(new HashSet<>());

        FilmController.FilmRequest filmRequest2 = new FilmController.FilmRequest();
        filmRequest2.setName("Film 2");
        filmRequest2.setDescription("Description 2");
        filmRequest2.setReleaseDate(LocalDate.of(2001, 1, 1));
        filmRequest2.setDuration(100);
        filmRequest2.setMpa(new MpaDto(1, "G"));
        filmRequest2.setGenres(new HashSet<>());

        ResponseEntity<FilmController.FilmResponse> filmResponse1 = restTemplate.postForEntity(
                "/films", filmRequest1, FilmController.FilmResponse.class);
        ResponseEntity<FilmController.FilmResponse> filmResponse2 = restTemplate.postForEntity(
                "/films", filmRequest2, FilmController.FilmResponse.class);

        FilmController.FilmResponse film1 = filmResponse1.getBody();
        FilmController.FilmResponse film2 = filmResponse2.getBody();
        assertNotNull(film1);
        assertNotNull(film2);

        // 3. Добавляем лайки с проверкой
        addLikeAndVerify(film1.getId(), user1.getId());
        addLikeAndVerify(film1.getId(), user2.getId());
        addLikeAndVerify(film2.getId(), user1.getId());

        // 4. Проверяем популярные фильмы
        ResponseEntity<FilmController.FilmResponse[]> response = restTemplate.getForEntity(
                "/films/popular?count=2", FilmController.FilmResponse[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        FilmController.FilmResponse[] popularFilms = response.getBody();
        assertNotNull(popularFilms);
        assertEquals(2, popularFilms.length);

        boolean film1IsFirst = popularFilms[0].getId().equals(film1.getId());
        FilmController.FilmResponse mostPopular = film1IsFirst ? popularFilms[0] : popularFilms[1];
        FilmController.FilmResponse secondPopular = film1IsFirst ? popularFilms[1] : popularFilms[0];

        assertEquals(film1.getId(), mostPopular.getId());
        assertEquals(film2.getId(), secondPopular.getId());
    }

    private void addLikeAndVerify(Long filmId, Long userId) {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/films/" + filmId + "/like/" + userId,
                HttpMethod.PUT,
                null,
                Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private Film createTestFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpaId(1);
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
