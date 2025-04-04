package ru.yandex.practicum.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.exception.NotFoundException;
import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь с ID: {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            log.error("Попытка обновления пользователя без ID");
            throw new ValidationException("ID пользователя не может быть null");
        }

        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с ID {} не найден", user.getId());
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }

        users.put(user.getId(), user);
        log.info("Обновлен пользователь с ID: {}", user.getId());
        return user;
    }

    @GetMapping
    public List<User> findAll() {
        log.debug("Запрошен список всех пользователей, количество: {}", users.size());
        return new ArrayList<>(users.values());
    }
}
