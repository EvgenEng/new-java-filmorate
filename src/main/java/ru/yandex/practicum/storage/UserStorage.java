package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.User;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User update(User user) throws NotFoundException;

    List<User> findAll();

    User findById(Long id) throws NotFoundException;

    boolean existsById(Long id);
}
