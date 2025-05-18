package ru.yandex.practicum.service;

import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.User;
import java.util.List;

public interface UserService {

    User addUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserById(Long id) throws NotFoundException;

    boolean existsById(Long userId);

    void addFriend(Long userId, Long friendId) throws NotFoundException;

    void removeFriend(Long userId, Long friendId) throws NotFoundException;

    List<User> getFriends(Long userId) throws NotFoundException;

    List<User> getCommonFriends(Long userId, Long otherId) throws NotFoundException;
}
