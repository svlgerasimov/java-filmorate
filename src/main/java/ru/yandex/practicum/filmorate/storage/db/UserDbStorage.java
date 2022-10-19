package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Qualifier("UserDbStorage")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT id, email, login, name, birthday FROM users;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public Optional<User> getById(long id) {
        String sql = "SELECT id, email, login, name, birthday FROM users WHERE id=?;";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public long addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        return simpleJdbcInsert.executeAndReturnKey(
                Map.of("email", user.getEmail(),
                        "login", user.getLogin(),
                        "name", user.getName(),
                        "birthday", Date.valueOf(user.getBirthday())))
                .longValue();
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?;";
        return jdbcTemplate.update(sql,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId()) > 0;
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        // Если уже была заявка в друзья от friendId к userId, нужно выставить isMutual в true.
        String sql = "UPDATE friends SET is_mutual=TRUE WHERE user_id=? AND friend_id=?;";
        boolean isMutual = jdbcTemplate.update(sql, friendId, userId) > 0;

        sql = "MERGE INTO friends (user_id, friend_id, is_mutual) VALUES (?, ?, ?);";
        return jdbcTemplate.update(sql, userId, friendId, isMutual) > 0;
    }

    @Override
    public boolean removeFriend(long userId, long friendId) {
        // Нужно убрать подтверждение у друга
        String sql = "UPDATE friends SET is_mutual=FALSE WHERE user_id=? AND friend_id=?;";
        jdbcTemplate.update(sql, friendId, userId);

        sql = "DELETE FROM friends WHERE user_id=? AND friend_id=?";
        return jdbcTemplate.update(sql, userId, friendId) > 0;
    }

    @Override
    public Collection<User> getFriends(long userId) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM users AS u " +
                "JOIN friends AS f ON u.id=f.friend_id " +
                "WHERE f.user_id=?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId);
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long otherId) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM users AS u " +
                "JOIN friends AS f1 ON u.id=f1.friend_id " +
                "JOIN friends AS f2 ON f1.friend_id=f2.friend_id " +
                "WHERE f1.user_id=? AND f2.user_id=?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId, otherId);
    }

    private static User makeUser(ResultSet resultSet) throws SQLException {
        Date birthday = resultSet.getDate("birthday");
        return new User(resultSet.getLong("id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                Objects.isNull(birthday) ? null : birthday.toLocalDate());
    }
}
