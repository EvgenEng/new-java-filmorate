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
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    @Override
    public List<User> getFriends(Long userId) throws NotFoundException {
        if (!existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        return jdbcTemplate.query(
                "SELECT u.* FROM users u JOIN friends f ON u.user_id = f.friend_id WHERE f.user_id = ?",
                (rs, rowNum) -> mapRowToUser(rs),
                userId
        );
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) throws NotFoundException {
        validateUsersExist(userId, otherId);

        return jdbcTemplate.query(
                "SELECT u.* FROM friends f1 " +
                        "JOIN friends f2 ON f1.friend_id = f2.friend_id " +
                        "JOIN users u ON f1.friend_id = u.user_id " +
                        "WHERE f1.user_id = ? AND f2.user_id = ?",
                (rs, rowNum) -> mapRowToUser(rs),
                userId, otherId
        );
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
        User user = userStorage.findById(id);
        if (user == null) {
            throw new NotFoundException("User with ID " + id + " not found");
        }
        return user;
    }

    @Override
    public boolean existsById(Long userId) {
        return userStorage.findById(userId) != null;
    }

    @Override
    public void addFriend(Long userId, Long friendId) throws NotFoundException {
        validateUsersExist(userId, friendId);

        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("User cannot add themselves as a friend");
        }

        String checkSql = "SELECT 1 FROM friends WHERE user_id = ? AND friend_id = ? LIMIT 1";
        boolean exists = jdbcTemplate.queryForList(checkSql, userId, friendId).size() > 0;

        if (!exists) {
            jdbcTemplate.update(
                    "INSERT INTO friends (user_id, friend_id, status_id) VALUES (?, ?, 1)",
                    userId, friendId
            );
            log.info("User {} added friend {}", userId, friendId);
        }
    }

    @Override
    public void removeFriend(Long userId, Long friendId) throws NotFoundException {
        validateUsersExist(userId, friendId);

        int deleted = jdbcTemplate.update(
                "DELETE FROM friends WHERE user_id = ? AND friend_id = ?",
                userId, friendId
        );

        if (deleted == 0) {
            log.warn("Friendship between {} and {} not found", userId, friendId);
        } else {
            log.info("User {} removed friend {}", userId, friendId);
        }
    }

    private void validateUsersExist(Long userId, Long friendId) throws NotFoundException {
        if (!existsById(userId)) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }
        if (!existsById(friendId)) {
            throw new NotFoundException("User with ID " + friendId + " not found");
        }
    }
}
