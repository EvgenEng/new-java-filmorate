package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }

    public User getUserById(Long id) {
        return userStorage.findById(id);
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }
}
