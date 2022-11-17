package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Qualifier("DirectorDbStorage")
@Slf4j
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getAll() {
        String directorsSql = "select d.director_id, d.name from director AS d";
        return jdbcTemplate.query(directorsSql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Optional<Director> getById(long id) {
        SqlRowSet dirRows = jdbcTemplate.queryForRowSet("select d.director_id, d.name from director AS d" +
                " where director_id = ?", id);
        if (dirRows.next()) {
            Director director = new Director(dirRows.getLong("director_id"),
                    dirRows.getString("name"));
            return Optional.of(director);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Director add(Director director) {
        String sqlQuery = "insert into director(name) " +
                "values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String directorName = director.getName();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"director_id"});
            stmt.setString(1, directorName);
            return stmt;
        }, keyHolder);
        return director.withId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    @Override
    public Optional<Director> update(Director director) {
        String updateDirectorSql = "update director set name = ? WHERE director_id = ?";
        return jdbcTemplate.update(updateDirectorSql, director.getName(), director.getId()) > 0 ?
                Optional.of(director) : Optional.empty();
    }

    @Override
    public boolean remove(long id) {
        String sql = "delete from director where director_id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    private static Director makeDirector(ResultSet resultSet) throws SQLException {
        return new Director(resultSet.getInt("director_id"), resultSet.getString("name"));
    }
}
