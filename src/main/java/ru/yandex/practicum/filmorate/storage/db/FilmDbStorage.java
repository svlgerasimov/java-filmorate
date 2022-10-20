package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Qualifier("FilmDbStorage")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "m.id AS mpa_id, m.name AS mpa_name " +
                "FROM film AS f " +
                "LEFT JOIN mpa AS m ON m.id=f.mpa_id;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Collection<Film> getMostPopularFilms(int count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "m.id AS mpa_id, m.name AS mpa_name " +
                "FROM film AS f " +
                "LEFT JOIN mpa AS m ON m.id=f.mpa_id " +
                "LEFT JOIN likes AS l ON l.film_id=f.id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(DISTINCT  l.user_id) DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    @Override
    public Optional<Film> getById(long id) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "m.id AS mpa_id, m.name AS mpa_name " +
                "FROM film AS f " +
                "LEFT JOIN mpa AS m ON m.id=f.mpa_id " +
                "WHERE f.id=?;";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);
        return films.isEmpty() ? Optional.empty() : Optional.of(films.get(0));
    }

    @Override
    public long addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("id");
        return simpleJdbcInsert.executeAndReturnKey(
                Map.of("name", film.getName(),
                        "description", film.getDescription(),
                        "release_date", Date.valueOf(film.getReleaseDate()),
                        "duration", film.getDuration(),
                        "mpa_id", film.getMpa().getId()))
                .longValue();
    }

    @Override
    public boolean updateFilm(Film film) {
        String sql = "UPDATE film " +
                "SET name=?, description=?, release_date=?, duration=?, mpa_id=? " +
                "WHERE id=?;";
        return jdbcTemplate.update(sql,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                Objects.nonNull(film.getMpa()) ? film.getMpa().getId() : null, film.getId()) > 0;
    }

    @Override
    public boolean addLike(long filmId, long userId) {
        String sql = "MERGE INTO likes (film_id, user_id) VALUES (?, ?);";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    @Override
    public boolean removeLike(long filmId, long userId) {
        String sql = "DELETE FROM likes WHERE film_id=? AND user_id=?;";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    private static Film makeFilm(ResultSet resultSet) throws SQLException {
        Date releaseDate = resultSet.getDate("release_date");

        int mpaId = resultSet.getInt("mpa_id");
        String mpaName = resultSet.getString("mpa_name");

        return new Film(resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                Objects.isNull(releaseDate) ? null : releaseDate.toLocalDate(),
                resultSet.getInt("duration"),
                mpaId == 0 ? null : new Mpa(mpaId, mpaName),
                null);
    }
}