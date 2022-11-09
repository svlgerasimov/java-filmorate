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
import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Qualifier("DirectorDbStorage")
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    private final Logger log = LoggerFactory.getLogger(DirectorDbStorage.class);

    @Override
    public List<Director> getAllDirectors() {
        String directorsSql = "select * from director";
        return jdbcTemplate.query(directorsSql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Optional<Director> getDirectorById(long id) {
        SqlRowSet dirRows = jdbcTemplate.queryForRowSet("select * from director where director_id = ?", id);
        if (dirRows.next()) {
            Director director = new Director(dirRows.getLong("director_id"),
                    dirRows.getString("name"));
            return Optional.of(director);
        } else {
            throw new NotFoundException("Такого режиссера нет");
        }
    }

    @Override
    public Optional<Director> addDirector(Director director) {
        String sqlQuery = "insert into director(name) " +
                "values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        log.info("вызван метод");
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("director id {}", director.getId());
        return getDirectorById(director.getId());
    }

    @Override
    public Optional<Director> updateDirector(Director director) {
        String updateDirectorSql = "update director set name = ? WHERE director_id = ?";
        checkDirectorExists(director.getId());
        jdbcTemplate.update(updateDirectorSql, director.getName(), director.getId());
        return getDirectorById(director.getId());
    }

    private static Director makeDirector(ResultSet resultSet) throws SQLException {
        return new Director(resultSet.getInt("director_id"), resultSet.getString("name"));
    }

    @Override
    public void deleteDirector(long id) {
        String sql = "delete from director where director_id = ?";
        jdbcTemplate.update(sql, id);
    }

    private void checkDirectorExists(long id) {
        getDirectorById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Director id=%s not found", id)));
    }
}
