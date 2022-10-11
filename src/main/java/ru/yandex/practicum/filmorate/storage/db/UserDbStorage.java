package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

@Component
@Qualifier("UserDbStorage")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserDbStorage implements UserStorage {

    private static final String TABLE_USER = "users";
    private static final String USER_FIELD_ID = "id";
    private static final String USER_FIELD_EMAIL = "email";
    private static final String USER_FIELD_LOGIN = "login";
    private static final String USER_FIELD_NAME = "name";
    private static final String USER_FIELD_BIRTHDAY = "birthday";

    private static final String TABLE_FRIENDS = "friends";
    private static final String FRIENDS_FIELD_ID = "id";
    private static final String FRIENDS_FIELD_USER_ID = "user_id";
    private static final String FRIENDS_FIELD_FRIEND_ID = "friend_id";
    private static final String FRIENDS_FIELD_IS_MUTUAL = "is_mutual";

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
//        return jdbcTemplate
//                .queryForStream(sql, (rs, rowNum) -> makeUser(rs), id)
//                .findFirst();
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

//    @Override
//    public void checkUserExists(long id) {
//
//    }

    @Override
    public User addUser(User user) {
//        String sqlQuery = String.format("INSERT INTO %s (%s, %s, %s, %s) values (?, ?, ?, ?);",
//                TABLE_USER,
//                USER_FIELD_EMAIL,
//                USER_FIELD_LOGIN,
//                USER_FIELD_NAME,
//                USER_FIELD_BIRTHDAY);
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//        jdbcTemplate.update(connection -> {
//            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
//            statement.setString(1, user.getEmail());
//            statement.setString(2, user.getLogin());
//            statement.setString(3, user.getName());
//            statement.setDate(4, Date.valueOf(user.getBirthday()));
//            return statement;
//        }, keyHolder);
//        return user.withId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_USER)
                .usingGeneratedKeyColumns(USER_FIELD_ID);
        return user.withId(
                simpleJdbcInsert.executeAndReturnKey(
                        Map.of(USER_FIELD_EMAIL, user.getEmail(),
                                USER_FIELD_LOGIN, user.getLogin(),
                                USER_FIELD_NAME, user.getName(),
                                USER_FIELD_BIRTHDAY, Date.valueOf(user.getBirthday())))
                .longValue());
    }

    @Override
    public User updateUser(User user) {
        String sql = String.format("UPDATE %s SET %s=?, %s=?, %s=?, %s=? WHERE %s=?;",
                TABLE_USER,
                USER_FIELD_EMAIL,
                USER_FIELD_LOGIN,
                USER_FIELD_NAME,
                USER_FIELD_BIRTHDAY,
                USER_FIELD_ID);
        jdbcTemplate.update(sql,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
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
        try {
            jdbcTemplate.update(sql, userId, friendId, isMutual);
        } catch (DataIntegrityViolationException exception) {
            // Нарушение ограничений в БД
//            throw new NotFoundException("Data integrity violation");
        }
        return true;
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
