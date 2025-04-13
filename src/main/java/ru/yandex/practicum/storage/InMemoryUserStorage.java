package ru.yandex.practicum.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;
    private final Map<Long, Set<Long>> friends = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User not found");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return user;
    }

    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }
        if (!users.containsKey(friendId)) {
            throw new NotFoundException("Friend with ID " + friendId + " not found");
        }

        friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }
        if (!users.containsKey(friendId)) {
            throw new NotFoundException("Friend with ID " + friendId + " not found");
        }

        Set<Long> userFriends = friends.get(userId);
        Set<Long> friendFriends = friends.get(friendId);
        if (userFriends != null) {
            userFriends.remove(friendId);
        }
        if (friendFriends != null) {
            friendFriends.remove(userId);
        }
    }

    @Override
    public List<User> getFriends(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }

        Set<Long> userFriends = friends.get(userId);
        if (userFriends == null) {
            return Collections.emptyList();
        }
        return userFriends.stream().map(users::get).toList();
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }
        if (!users.containsKey(otherId)) {
            throw new NotFoundException("User with ID " + otherId + " not found");
        }

        Set<Long> userFriends = new HashSet<>(friends.getOrDefault(userId, Collections.emptySet()));
        Set<Long> otherFriends = new HashSet<>(friends.getOrDefault(otherId, Collections.emptySet()));
        userFriends.retainAll(otherFriends);
        return userFriends.stream().map(users::get).toList();
    }
}
