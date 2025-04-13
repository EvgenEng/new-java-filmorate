package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(User user);

    List<User> findAll();

    User findById(Long id);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    List<User> getCommonFriends(Long userId, Long otherId);
}
