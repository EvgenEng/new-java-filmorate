package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> friendships = new HashMap<>();

    public User addUser(User user) {
        User createdUser = userStorage.create(user);
        log.info("User {} added", createdUser.getId());
        return createdUser;
    }

    public User updateUser(User user) {
        User updatedUser = userStorage.update(user);
        log.info("User {} updated", updatedUser.getId());
        return updatedUser;
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public User getUserById(Long id) {
        return userStorage.findById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.findById(userId);
        userStorage.findById(friendId);

        friendships.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friendships.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
        log.info("User {} added friend {}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }

        if (!userStorage.existsById(friendId)) {
            throw new NotFoundException("User with ID " + friendId + " not found");
        }

        friendships.getOrDefault(userId, Collections.emptySet()).remove(friendId);
        friendships.getOrDefault(friendId, Collections.emptySet()).remove(userId);
    }

    public List<User> getFriends(Long userId) {
        userStorage.findById(userId);
        Set<Long> userFriends = friendships.get(userId);
        if (userFriends == null) {
            return Collections.emptyList();
        }
        return userFriends.stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        userStorage.findById(userId);
        userStorage.findById(otherId);

        Set<Long> commonIds = new HashSet<>(friendships.getOrDefault(userId, Set.of()));
        commonIds.retainAll(friendships.getOrDefault(otherId, Set.of()));

        return commonIds.stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }
}
