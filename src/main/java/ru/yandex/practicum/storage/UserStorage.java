package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User update(User user);

    List<User> findAll();

    User findById(Long id);

    boolean existsById(Long id);
}
