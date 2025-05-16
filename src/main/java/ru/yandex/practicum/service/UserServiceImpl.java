package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    @Override
    public List<User> getFriends(Long userId) throws NotFoundException {
        validateUserExists(userId);
        return userStorage.getFriends(userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) throws NotFoundException {
        validateUserExists(userId);
        validateUserExists(otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }

    @Override
    public User addUser(User user) {
        User createdUser = userStorage.create(user);
        log.info("User {} added", createdUser.getId());
        return createdUser;
    }

    @Override
    public User updateUser(User user) {
        User updatedUser = userStorage.update(user);
        log.info("User {} updated", updatedUser.getId());
        return updatedUser;
    }

    @Override
    public User getUserById(Long id) throws NotFoundException {
        return userStorage.findById(id);
    }

    @Override
    public boolean existsById(Long userId) {
        return userStorage.existsById(userId);
    }

    @Override
    public void addFriend(Long userId, Long friendId) throws NotFoundException {
        validateUserExists(userId);
        validateUserExists(friendId);

        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("User cannot add themselves as a friend");
        }

        userStorage.addFriend(userId, friendId);
        log.info("User {} added friend {}", userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) throws NotFoundException {
        validateUserExists(userId);
        validateUserExists(friendId);

        userStorage.removeFriend(userId, friendId);
        log.info("User {} removed friend {}", userId, friendId);
    }

    private void validateUserExists(Long userId) throws NotFoundException {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }
    }
}
