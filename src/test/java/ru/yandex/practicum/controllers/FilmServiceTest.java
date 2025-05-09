package ru.yandex.practicum.controllers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmServiceTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testCreateFilm() {
        String filmJson = "{\"name\":\"Inception\",\"description\":\"A thief who steals corporate secrets\"," +
                "\"releaseDate\":\"2010-07-16\",\"duration\":148,\"mpa\":3,\"genres\":[1,2]}";

        given()
                .contentType(ContentType.JSON)
                .body(filmJson)
                .when()
                .post("/films")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Inception"))
                .body("mpa", equalTo(3));
    }

    @Test
    public void testCreateUser() {
        String userJson = "{\"email\":\"user@example.com\",\"login\":\"user123\"," +
                "\"name\":\"John Doe\",\"birthday\":\"1990-01-01\"}";

        given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }

    @Test
    public void testGetFilmById() {
        // Предполагается, что фильм с ID 1 существует
        given()
                .when()
                .get("/films/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", notNullValue());
    }

    @Test
    public void testCreateFilm_ValidData() {
        String validFilmJson = "{\"name\":\"Valid Film\",\"description\":\"A valid description\"," +
                "\"releaseDate\":\"2025-05-09\",\"duration\":90,\"mpa\":1,\"genres\":[]}";

        given()
                .contentType(ContentType.JSON)
                .body(validFilmJson)
                .when()
                .post("/films")
                .then()
                .statusCode(201) // Ожидаем статус 201 для успешного создания
                .body("id", notNullValue())
                .body("name", equalTo("Valid Film"));
    }

    @Test
    public void testInvalidFilmCreation_EmptyName() {
        String invalidFilmJson = "{\"name\":\"\",\"description\":\"Valid description\"," +
                "\"releaseDate\":\"2023-01-01\",\"duration\":90,\"mpa\":1,\"genres\":[]}";

        given()
                .contentType(ContentType.JSON)
                .body(invalidFilmJson)
                .when()
                .post("/films")
                .then()
                .statusCode(400)
                .body("message", containsString("Validation error")) // Изменено с "failed" на "error"
                .body("errors.name", notNullValue()); // Дополнительная проверка наличия ошибки для поля name
    }
}
