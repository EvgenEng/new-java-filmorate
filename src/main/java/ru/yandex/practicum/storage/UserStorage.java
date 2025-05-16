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

    List<User> getFriends(Long userId);

    List<User> getCommonFriends(Long userId, Long otherId);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    boolean friendshipExists(Long userId, Long friendId);
}
