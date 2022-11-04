package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.inmemory.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Qualifier("DirectorDbStorage")
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    private final Logger log = LoggerFactory.getLogger(DirectorDbStorage.class);


    @Override
    public List<Director> getAllDirectors() {
        String directorsSql = "select * from director";
        return jdbcTemplate.query(directorsSql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Director getDirectorById(long id) {
        SqlRowSet dirRows = jdbcTemplate.queryForRowSet("select * from director" +
                " where director_id = ?", id);
        if (dirRows.next()) {
            return new Director(dirRows.getLong("director_id"), dirRows.getString("name"));
        }
        throw new NotFoundException("Такого режиссера нет");
    }

    @Override
    public Director addDirector(Director director) {
        String sqlQuery = "insert into director(name) " +
                "values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        log.info("вызван метод");
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("director id {}", director.getId());
        return getDirectorById(director.getId());
    }

    @Override
    public Director updateDirector(Director director) {
        String updateDirectorSql = "update director set name = ?";
        List<Director> directors = getAllDirectors();
        if (directors.contains(director)) {
            jdbcTemplate.update(updateDirectorSql, director.getName());
            return getDirectorById(director.getId());
        }
        throw new NotFoundException("Такого режиссера нет");
    }

    public Director makeDirector(ResultSet resultSet) throws SQLException {
        return new Director(resultSet.getLong("director_id"), resultSet.getString("name"));
    }

    @Override
    public void deleteDirector(long id) {
        String sql = "delete from director where director_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
