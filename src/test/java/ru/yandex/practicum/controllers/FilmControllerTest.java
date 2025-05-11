package ru.yandex.practicum.controllers;

import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.http.*;
//import ru.yandex.practicum.exception.ErrorResponse;
import ru.yandex.practicum.model.Film;
//import ru.yandex.practicum.model.MpaRating;
import ru.yandex.practicum.model.User;

import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.Objects;

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

    /*@Test
    void shouldCreateFilm() {
        String filmJson = "{\"name\":\"Тестовый фильм\",\"description\":\"Тестовое описание\"," +
                "\"releaseDate\":\"2023-01-01\",\"duration\":90,\"mpa\":1}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(filmJson, headers);

        ResponseEntity<Film> response = restTemplate.postForEntity(
                "/films",
                request,
                Film.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Film createdFilm = response.getBody();
        assertNotNull(createdFilm);
        assertEquals(1, createdFilm.getMpaId());
    }*/

    /*@Test
    void shouldGetAllFilms() {
        Film simpleFilm = new Film();
        simpleFilm.setName("Тестовый фильм");
        simpleFilm.setDescription("Тестовое описание");
        simpleFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        simpleFilm.setDuration(90);
        simpleFilm.setMpaId(1);

        ResponseEntity<Film> createResponse = restTemplate.postForEntity(
                "/films",
                simpleFilm,
                Film.class
        );
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        ResponseEntity<Film[]> response = restTemplate.getForEntity("/films", Film[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);

        // Дополнительные проверки
        Film createdFilm = createResponse.getBody();
        Film[] allFilms = response.getBody();
        assertNotNull(createdFilm);
        assertNotNull(allFilms);
        assertTrue(Arrays.stream(allFilms).anyMatch(f ->
                f.getId().equals(createdFilm.getId()) &&
                        f.getName().equals(createdFilm.getName())
        ));
    }*/

    /*@Test
    void shouldCreateSimpleFilm() {
        Film simpleFilm = new Film();
        simpleFilm.setName("Простой фильм");
        simpleFilm.setDescription("Простое описание");
        simpleFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        simpleFilm.setDuration(90);
        simpleFilm.setMpaId(1);

        ResponseEntity<Film> response = restTemplate.postForEntity(
                "/films",
                simpleFilm,
                Film.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(MpaRating.G, response.getBody().getMpaRating());
    }*/

    /*@Test
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
    }*/

    /*@Test
    void shouldNotCreateFilmWithLongDescription() {
        testFilm.setDescription("A".repeat(201));

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/films",
                testFilm,
                ErrorResponse.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }*/

    /*@Test
    void shouldReturnNotFoundForUnknownFilmUpdate() {
        Film unknownFilm = new Film();
        unknownFilm.setId(999L);
        unknownFilm.setName("Unknown Film");
        unknownFilm.setDescription("Description");
        unknownFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        unknownFilm.setDuration(120);
        unknownFilm.setMpaId(1);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/films/" + unknownFilm.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(unknownFilm),
                ErrorResponse.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Film not found", response.getBody().getMessage());
    }*/

    /*@Test
    void testAddLikeToUnknownFilm() {
        User createdUser = restTemplate.postForEntity("/users", testUser, User.class).getBody();
        assertNotNull(createdUser);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/films/999/like/" + createdUser.getId(),
                HttpMethod.PUT,
                null,
                ErrorResponse.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("Film not found"));
    }*/

    /*@Test
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
    } */

    /*@Test
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

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }*/

    /*@Test
    void shouldRemoveLike() {
        User createdUser = restTemplate.postForEntity("/users", testUser, User.class).getBody();
        Film createdFilm = restTemplate.postForEntity("/films", testFilm, Film.class).getBody();
        assertNotNull(createdUser);
        assertNotNull(createdFilm);

        // Сначала добавляем лайк
        restTemplate.put("/films/" + createdFilm.getId() + "/like/" + createdUser.getId(), null);

        // Затем удаляем
        ResponseEntity<String> response = restTemplate.exchange(
                "/films/" + createdFilm.getId() + "/like/" + createdUser.getId(),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }*/

    /*@Test
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
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().getMessage());
    }*/

    /*@Test
    void shouldGetPopularFilms() {
        // Создаем тестовые данные
        Film film1 = createTestFilm("Film 1");
        Film film2 = createTestFilm("Film 2");
        User user1 = createTestUser("User 1");
        User user2 = createTestUser("User 2");

        // Добавляем лайки
        restTemplate.put("/films/" + film1.getId() + "/like/" + user1.getId(), null);
        restTemplate.put("/films/" + film1.getId() + "/like/" + user2.getId(), null);
        restTemplate.put("/films/" + film2.getId() + "/like/" + user1.getId(), null);

        // Получаем популярные фильмы
        ResponseEntity<Film[]> response = restTemplate.getForEntity(
                "/films/popular?count=2",
                Film[].class
        );

        // Проверяем статус ответа
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
        assertEquals(film1.getId(), response.getBody()[0].getId());

        // Дополнительная отладочная информация
        System.out.println("Popular films: " + Arrays.toString(response.getBody()));
    }*/

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
