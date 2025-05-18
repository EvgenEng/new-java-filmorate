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
import org.springframework.http.*;
import ru.yandex.practicum.exception.ErrorResponse;
import ru.yandex.practicum.model.User;

import java.time.LocalDate;
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
        assertNotNull(created);
        created.setName("Updated Name");

        ResponseEntity<User> response = restTemplate.exchange(
                "/users",
                HttpMethod.PUT,
                new HttpEntity<>(created),
                User.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
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
        testUser.setId(999L);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/users",
                HttpMethod.PUT,
                new HttpEntity<>(testUser),
                ErrorResponse.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldAddAndRemoveFriend() {
        // Create first user
        User user1 = restTemplate.postForEntity("/users", testUser, User.class).getBody();
        assertNotNull(user1);

        // Create second user
        User user2 = new User();
        user2.setEmail("friend@example.com");
        user2.setLogin("friendlogin");
        user2.setName("Friend User");
        user2.setBirthday(LocalDate.of(1995, 1, 1));
        User friend = restTemplate.postForEntity("/users", user2, User.class).getBody();
        assertNotNull(friend);

        // Add friend
        ResponseEntity<Void> addResponse = restTemplate.exchange(
                "/users/" + user1.getId() + "/friends/" + friend.getId(),
                HttpMethod.PUT,
                null,
                Void.class
        );
        assertEquals(HttpStatus.OK, addResponse.getStatusCode());

        // Remove friend
        ResponseEntity<Void> removeResponse = restTemplate.exchange(
                "/users/" + user1.getId() + "/friends/" + friend.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.OK, removeResponse.getStatusCode());
    }
}
