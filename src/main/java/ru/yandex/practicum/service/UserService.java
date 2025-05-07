package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

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

        String sql = "INSERT INTO friends (user_id, friend_id, status_id) VALUES (?, ?, 1) " +
                "ON CONFLICT (user_id, friend_id) DO NOTHING";
        jdbcTemplate.update(sql, userId, friendId);
        log.info("User {} added friend {}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        int deleted = jdbcTemplate.update(sql, userId, friendId);

        if (deleted == 0) {
            throw new NotFoundException("Friendship not found");
        }
        log.info("User {} removed friend {}", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        userStorage.findById(userId);

        String sql = "SELECT u.* FROM friends f " +
                "JOIN users u ON f.friend_id = u.user_id " +
                "WHERE f.user_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        }, userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        userStorage.findById(userId);
        userStorage.findById(otherId);

        String sql = "SELECT u.* FROM friends f1 " +
                "JOIN friends f2 ON f1.friend_id = f2.friend_id " +
                "JOIN users u ON f1.friend_id = u.user_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        }, userId, otherId);
    }
}
