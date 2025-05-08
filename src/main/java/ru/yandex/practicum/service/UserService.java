package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        // Проверка, что пользователь не добавляет сам себя
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("User cannot add themselves as a friend");
        }

        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("User or friend not found");
        }

        // Более эффективная проверка существования дружбы
        String checkSql = "SELECT 1 FROM friends WHERE user_id = ? AND friend_id = ? LIMIT 1";
        boolean exists = jdbcTemplate.queryForList(checkSql, userId, friendId).size() > 0;

        if (!exists) {
            String insertSql = "INSERT INTO friends (user_id, friend_id, status_id) VALUES (?, ?, 1)";
            jdbcTemplate.update(insertSql, userId, friendId);
            log.info("User {} added friend {}", userId, friendId);
        } else {
            log.info("Friendship between {} and {} already exists", userId, friendId);
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        // Проверяем существование пользователей
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("User or friend not found");
        }

        // Удаляем дружбу в обе стороны (если используется двусторонняя дружба)
        String sql = "DELETE FROM friends WHERE (user_id = ? AND friend_id = ?)";
        int deleted = jdbcTemplate.update(sql, userId, friendId);

        if (deleted == 0) {
            throw new NotFoundException("Friendship not found");
        }
        log.info("User {} removed friend {}", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("User not found");
        }

        String sql = "SELECT u.* FROM users u " +
                "JOIN friends f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs), userId);
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = userStorage.findById(userId);
        User otherUser = userStorage.findById(otherId);
        if (user == null || otherUser == null) {
            throw new NotFoundException("User not found");
        }

        String sql = "SELECT u.* FROM friends f1 " +
                "JOIN friends f2 ON f1.friend_id = f2.friend_id " +
                "JOIN users u ON f1.friend_id = u.user_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs), userId, otherId);
    }
}
