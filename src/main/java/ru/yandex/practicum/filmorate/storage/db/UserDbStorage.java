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

    public static final String TABLE_USER = "users";
    public static final String USER_FIELD_ID = "id";
    public static final String USER_FIELD_EMAIL = "email";
    public static final String USER_FIELD_LOGIN = "login";
    public static final String USER_FIELD_NAME = "name";
    public static final String USER_FIELD_BIRTHDAY = "birthday";

    public static final String TABLE_FRIENDS = "friends";
    public static final String FRIENDS_FIELD_USER_ID = "user_id";
    public static final String FRIENDS_FIELD_FRIEND_ID = "friend_id";
    public static final String FRIENDS_FIELD_IS_MUTUAL = "is_mutual";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> getAllUsers() {
        String sql = String.format("SELECT %s, %s, %s, %s, %s FROM %s;",
                USER_FIELD_ID,
                USER_FIELD_EMAIL,
                USER_FIELD_LOGIN,
                USER_FIELD_NAME,
                USER_FIELD_BIRTHDAY,
                TABLE_USER);
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public Optional<User> getById(long id) {
        String sql = String.format("SELECT %s, %s, %s, %s, %s FROM %s WHERE %s = ?;",
                USER_FIELD_ID,
                USER_FIELD_EMAIL,
                USER_FIELD_LOGIN,
                USER_FIELD_NAME,
                USER_FIELD_BIRTHDAY,
                TABLE_USER,
                USER_FIELD_ID);
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public long addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_USER)
                .usingGeneratedKeyColumns(USER_FIELD_ID);
        return simpleJdbcInsert.executeAndReturnKey(
                Map.of(USER_FIELD_EMAIL, user.getEmail(),
                        USER_FIELD_LOGIN, user.getLogin(),
                        USER_FIELD_NAME, user.getName(),
                        USER_FIELD_BIRTHDAY, Date.valueOf(user.getBirthday())))
                .longValue();
    }

    @Override
    public boolean updateUser(User user) {
        String sql = String.format("UPDATE %s SET %s=?, %s=?, %s=?, %s=? WHERE %s=?;",
                TABLE_USER,
                USER_FIELD_EMAIL,
                USER_FIELD_LOGIN,
                USER_FIELD_NAME,
                USER_FIELD_BIRTHDAY,
                USER_FIELD_ID);
        return jdbcTemplate.update(sql,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId()) > 0;
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        // Если уже была заявка в друзья от friendId к userId, нужно выставить isMutual в true.
        String sql = String.format("UPDATE %s SET %s=TRUE WHERE %s=? AND %s=?;",
                TABLE_FRIENDS,
                FRIENDS_FIELD_IS_MUTUAL,
                FRIENDS_FIELD_USER_ID,
                FRIENDS_FIELD_FRIEND_ID);
        boolean isMutual = jdbcTemplate.update(sql, friendId, userId) > 0;

        sql = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?);",
                TABLE_FRIENDS,
                FRIENDS_FIELD_USER_ID,
                FRIENDS_FIELD_FRIEND_ID,
                FRIENDS_FIELD_IS_MUTUAL);
        return jdbcTemplate.update(sql, userId, friendId, isMutual) > 0;
    }

    @Override
    public boolean removeFriend(long userId, long friendId) {
        // Нужно убрать подтверждение у друга
        String sql = String.format("UPDATE %s SET %s=FALSE WHERE %s=? AND %s=?;",
                TABLE_FRIENDS,
                FRIENDS_FIELD_IS_MUTUAL,
                FRIENDS_FIELD_USER_ID,
                FRIENDS_FIELD_FRIEND_ID);
        jdbcTemplate.update(sql, friendId, userId);

        sql = String.format("DELETE FROM %s WHERE %s=? AND %s=?;",
                TABLE_FRIENDS,
                FRIENDS_FIELD_USER_ID,
                FRIENDS_FIELD_FRIEND_ID);
        return jdbcTemplate.update(sql, userId, friendId) > 0;
    }

    @Override
    public Collection<User> getFriends(long userId) {
        String sql =
                String.format("SELECT u.%s, u.%s, u.%s, u.%s, u.%s ",
                        USER_FIELD_ID,
                        USER_FIELD_EMAIL,
                        USER_FIELD_LOGIN,
                        USER_FIELD_NAME,
                        USER_FIELD_BIRTHDAY) +
                        String.format("FROM %s AS u ",
                                TABLE_USER) +
                        String.format("JOIN %s AS f ON u.%s = f.%s ",
                                TABLE_FRIENDS,
                                USER_FIELD_ID,
                                FRIENDS_FIELD_FRIEND_ID) +
                        String.format("WHERE f.%s=? ;",
                                FRIENDS_FIELD_USER_ID);
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId);
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long otherId) {
        String sql =
                String.format("SELECT u.%s, u.%s, u.%s, u.%s, u.%s ",
                        USER_FIELD_ID,
                        USER_FIELD_EMAIL,
                        USER_FIELD_LOGIN,
                        USER_FIELD_NAME,
                        USER_FIELD_BIRTHDAY) +
                        String.format("FROM %s AS u ",
                                TABLE_USER) +
                        String.format("JOIN %s AS f1 ON u.%s = f1.%s ",
                                TABLE_FRIENDS,
                                USER_FIELD_ID,
                                FRIENDS_FIELD_FRIEND_ID) +
                        String.format("JOIN %s AS f2 ON f1.%s = f2.%s ",
                                TABLE_FRIENDS,
                                FRIENDS_FIELD_FRIEND_ID,
                                FRIENDS_FIELD_FRIEND_ID) +
                        String.format("WHERE f1.%s=? AND f2.%s=?;",
                                FRIENDS_FIELD_USER_ID,
                                FRIENDS_FIELD_USER_ID);
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId, otherId);
    }

    private static User makeUser(ResultSet resultSet) throws SQLException {
        Date birthday = resultSet.getDate(USER_FIELD_BIRTHDAY);
        return new User(resultSet.getLong(USER_FIELD_ID),
                resultSet.getString(USER_FIELD_EMAIL),
                resultSet.getString(USER_FIELD_LOGIN),
                resultSet.getString(USER_FIELD_NAME),
                Objects.isNull(birthday) ? null : birthday.toLocalDate());
    }
}
