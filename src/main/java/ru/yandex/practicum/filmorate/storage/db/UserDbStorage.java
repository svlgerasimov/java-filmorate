package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
                        new MapSqlParameterSource()
                                .addValue("email", user.getEmail())
                                .addValue("login", user.getLogin())
                                .addValue("name", user.getName())
                                .addValue("birthday", Date.valueOf(user.getBirthday())))
                .longValue();
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?;";
        return jdbcTemplate.update(sql,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId()) > 0;
    }

    public static User makeUser(ResultSet resultSet) throws SQLException {
        Date birthday = resultSet.getDate("birthday");
        return new User(resultSet.getLong("id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                Objects.isNull(birthday) ? null : birthday.toLocalDate());
    }
}
