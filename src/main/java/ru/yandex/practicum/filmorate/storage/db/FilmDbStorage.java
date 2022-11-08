package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Qualifier("FilmDbStorage")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, d.director_id, " +
                "d.name, m.id AS mpa_id, m.name AS mpa_name, " +
                "COUNT(DISTINCT l.user_id) AS rate " +
                "FROM film AS f " +
                "LEFT JOIN film_directors AS fd ON fd.film_id = f.id " +
                "LEFT JOIN director AS d ON d.director_id = fd.director_id " +
                "LEFT JOIN mpa AS m ON m.id=f.mpa_id " +
                "LEFT JOIN likes AS l ON l.film_id=f.id " +
                "GROUP BY f.id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Collection<Film> getMostPopularFilms(int count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "m.id AS mpa_id, m.name AS mpa_name, " +
                "COUNT(DISTINCT l.user_id) AS rate " +
                "FROM film AS f " +
                "LEFT JOIN mpa AS m ON m.id=f.mpa_id " +
                "LEFT JOIN likes AS l ON l.film_id=f.id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(DISTINCT  l.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    @Override
    public Optional<Film> getById(long id) { // добавить директора
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, d.director_id, " +
                "d.name, m.id AS mpa_id, m.name AS mpa_name, " +
                "COUNT(DISTINCT l.user_id) AS rate " +
                "FROM film AS f " +
                "LEFT JOIN mpa AS m ON m.id=f.mpa_id " +
                "LEFT JOIN likes AS l ON l.film_id=f.id " +
                "LEFT JOIN film_directors AS fd ON fd.film_id = f.id " +
                "LEFT JOIN director AS d ON d.director_id = fd.director_id " +
                "WHERE f.id=? GROUP BY f.id, d.director_id";
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
                "SET name=?, description=?, release_date=?, duration=?, mpa_id=?" +
                "WHERE id=?;";
        return jdbcTemplate.update(sql,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                Objects.nonNull(film.getMpa()) ? film.getMpa().getId() : null,
                film.getId()) > 0;
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
                    new ArrayList<>(),
                    resultSet.getInt("rate"), new ArrayList<>(0));
        }

    public List<Film> getDirectorFilms(long directorId){
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, d.director_id, " +
                "d.name, m.id AS mpa_id, m.name AS mpa_name, COUNT(DISTINCT l.user_id) AS rate, g.name, g.id, fg.film_id, " +
                "fg.genre_id FROM film AS f " +
                "LEFT JOIN film_genre AS fg ON f.id = fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.id " +
                "LEFT JOIN mpa AS m ON m.id=f.mpa_id " +
                "JOIN film_directors AS fd ON fd.film_id = f.id " +
                "JOIN director AS d ON d.director_id = fd.director_id " +
                "LEFT JOIN likes AS l ON l.film_id=f.id " +
                "WHERE d.director_id = ? GROUP BY f.id, d.director_id";
         return jdbcTemplate.query(sql, (rs, rowNum) -> makeFullFilm(rs), directorId);
    }

    public Film makeFullFilm(ResultSet resultSet) throws SQLException {
        Date releaseDate = resultSet.getDate("release_date");
        int mpaId = resultSet.getInt("mpa_id");
        String mpaName = resultSet.getString("mpa_name");

        ArrayList<Director> directors = new ArrayList<>();
        directors.add(new Director(resultSet.getLong("director_id"),
                resultSet.getString("director.name")));

        Collection<Genre> genres = new ArrayList<>();
        genres.add(new Genre(resultSet.getInt("genre.id"),
                resultSet.getString("genre.name")));

            return new Film(resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    Objects.isNull(releaseDate) ? null : releaseDate.toLocalDate(),
                    resultSet.getInt("duration"),
                    mpaId == 0 ? null : new Mpa(mpaId, mpaName),
                    genres,
                    resultSet.getInt("rate"), directors);
    }
}
