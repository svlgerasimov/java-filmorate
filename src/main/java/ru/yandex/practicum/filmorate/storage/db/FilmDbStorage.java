package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Qualifier("FilmDbStorage")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "m.id AS mpa_id, m.name AS mpa_name, " +
                "COUNT(DISTINCT l.user_id) AS rate " +
                "FROM film AS f " +
                "LEFT JOIN mpa AS m ON m.id=f.mpa_id " +
                "LEFT JOIN likes AS l ON l.film_id=f.id " +
                "GROUP BY f.id;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Collection<Film> getMostPopularFilms(int count, Long genreId, Integer year) {
        if (genreId == null && year == null) {
            return getMostPopularFilmsByFilter(count);
        } else if (genreId == null) {
            return getMostPopularFilmsByFilter(count, year);
        } else if (year == null) {
            return getMostPopularFilmsByFilter(count, genreId);
        } else {
            return getMostPopularFilmsByFilter(count, genreId, year);
        }
    }

    @Override
    public Collection<Film> getFilmsLikedByUser(long userId) {
        //Фильмы, которым пользователь поставил лайк
        String sql = "WITH rates AS\n" +
                "    (SELECT f.id AS film_id, COUNT(DISTINCT l.user_id) AS rate\n" +
                "     FROM film AS f\n" +
                "     LEFT JOIN likes AS l ON l.film_id = f.id\n" +
                "     GROUP BY f.id)\n" +
                "SELECT f.id, f.name, f.description, f.release_date, f.duration,\n" +
                "       m.id AS mpa_id, m.name AS mpa_name, r.rate\n" +
                "FROM film AS f\n" +
                "    LEFT JOIN rates AS r ON r.film_id = f.id\n" +
                "    LEFT JOIN mpa AS m ON m.id = f.mpa_id\n" +
                "    LEFT JOIN likes AS l ON l.film_id = f.id\n" +
                "WHERE l.user_id = ?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), userId);
    }

    @Override
    public Optional<Film> getById(long id) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "m.id AS mpa_id, m.name AS mpa_name, " +
                "COUNT(DISTINCT l.user_id) AS rate " +
                "FROM film AS f " +
                "LEFT JOIN mpa AS m ON m.id=f.mpa_id " +
                "LEFT JOIN likes AS l ON l.film_id=f.id " +
                "WHERE f.id=? " +
                "GROUP BY f.id;";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);
        return films.isEmpty() ? Optional.empty() : Optional.of(films.get(0));
    }

    @Override
    public long addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("id");
        MapSqlParameterSource mapSqlParameterSource =  new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", Date.valueOf(film.getReleaseDate()))
                .addValue("duration", film.getDuration());
        if (Objects.nonNull(film.getMpa())) {
            mapSqlParameterSource.addValue("mpa_id", film.getMpa().getId());
        }
        return simpleJdbcInsert.executeAndReturnKey(mapSqlParameterSource).longValue();
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
    public void removeFilm(long filmId) {
        String sql = "DELETE FROM film WHERE id = ?;";
        jdbcTemplate.update(sql, filmId);
    }

    private Collection<Film> getMostPopularFilmsByFilter(int count, Long genreId, Integer year) {
        return jdbcTemplate.query(
                "SELECT  film.id, film.name, description, release_date, duration, mpa_id, " +
                        "COUNT(l.FILM_ID) as rate, m.NAME as mpa_name " +
                        "FROM film " +
                        "LEFT JOIN likes AS l on film.id = l.film_id " +
                        "LEFT JOIN film_genre AS fg on film.id = fg.film_id " +
                        "LEFT JOIN mpa AS m on film.mpa_id = m.id " +
                        "WHERE YEAR(release_date) = ? AND fg.genre_id = ?" +
                        "GROUP BY  film.id " +
                        "ORDER BY rate DESC " +
                        "LIMIT ?;",
                (rs, rowNum) -> makeFilm(rs), year, genreId, count);
    }

    private Collection<Film> getMostPopularFilmsByFilter(int count, Long genreId) {
        return jdbcTemplate.query(
                "SELECT  film.id, film.name, description, release_date, duration, mpa_id, " +
                        "COUNT(l.FILM_ID) as rate, m.NAME as mpa_name " +
                        "FROM film " +
                        "LEFT JOIN likes AS l on film.id = l.film_id " +
                        "LEFT JOIN film_genre AS fg on film.id = fg.film_id " +
                        "LEFT JOIN mpa AS m on film.mpa_id = m.id " +
                        "WHERE fg.genre_id = ?" +
                        "GROUP BY  film.id " +
                        "ORDER BY rate DESC " +
                        "LIMIT ?;",
                (rs, rowNum) -> makeFilm(rs), genreId, count);
    }

    private Collection<Film> getMostPopularFilmsByFilter(int count, Integer year) {
        return jdbcTemplate.query(
                "SELECT  film.id, film.name, description, release_date, duration, mpa_id, " +
                        "COUNT(l.FILM_ID) as rate, m.NAME as mpa_name " +
                        "FROM film " +
                        "LEFT JOIN likes AS l on film.id = l.film_id " +
                        "LEFT JOIN film_genre AS fg on film.id = fg.film_id " +
                        "LEFT JOIN mpa AS m on film.mpa_id = m.id " +
                        "WHERE YEAR(release_date) = ?" +
                        "GROUP BY  film.id " +
                        "ORDER BY rate DESC " +
                        "LIMIT ?;",
                (rs, rowNum) -> makeFilm(rs), year, count);
    }

    private Collection<Film> getMostPopularFilmsByFilter(int count) {
        return jdbcTemplate.query(
                "SELECT  film.id, film.name, description, release_date, duration, mpa_id, " +
                        "COUNT(l.FILM_ID) as rate, m.NAME as mpa_name " +
                        "FROM film " +
                        "LEFT JOIN likes AS l on film.id = l.film_id " +
                        "LEFT JOIN film_genre AS fg on film.id = fg.film_id " +
                        "LEFT JOIN mpa AS m on film.mpa_id = m.id " +
                        "GROUP BY  film.id " +
                        "ORDER BY rate DESC " +
                        "LIMIT ?;",
                (rs, rowNum) -> makeFilm(rs), count);
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
                null,
                resultSet.getInt("rate"));
    }
}
