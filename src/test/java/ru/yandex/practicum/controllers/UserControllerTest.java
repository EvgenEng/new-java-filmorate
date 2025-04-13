package ru.yandex.practicum.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.model.User;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private Validator validator;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Инициализация валидатора
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testlogin");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void shouldCreateUser() {
        ResponseEntity<User> response = restTemplate.postForEntity("/users", testUser, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("testlogin", response.getBody().getLogin());
    }

    @Test
    void shouldGetAllUsers() {
        restTemplate.postForEntity("/users", testUser, User.class);

        ResponseEntity<User[]> response = restTemplate.getForEntity("/users", User[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void shouldUpdateUser() {
        User created = restTemplate.postForEntity("/users", testUser, User.class).getBody();
        created.setName("Updated Name");

        ResponseEntity<User> response = restTemplate.postForEntity("/users", created, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Updated Name", response.getBody().getName());
    }

    @Test
    void shouldNotCreateUserWithEmptyEmail() {
        testUser.setEmail("");
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldNotCreateUserWithInvalidEmail() {
        testUser.setEmail("invalid-email");
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldNotCreateUserWithFutureBirthday() {
        testUser.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsEmpty() {
        testUser.setName("");
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        assertTrue(violations.isEmpty());
        assertEquals("testlogin", testUser.getName());
    }

    @Test
    void shouldReturnNotFoundForUnknownUserUpdate() {
        testUser.setId(999L); // Устанавливаем ID несуществующего пользователя

        ResponseEntity<Map> response = restTemplate.exchange("/users", HttpMethod.PUT,
                new HttpEntity<>(testUser), Map.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
