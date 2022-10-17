package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Array;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Qualifier("FilmDbStorage")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FilmDbStorage implements FilmStorage {
    
    public static final String TABLE_FILM = "film";
    public static final String FILM_FIELD_ID = "id";
    public static final String FILM_FIELD_NAME = "name";
    public static final String FILM_FIELD_DESCRIPTION = "description";
    public static final String FILM_FIELD_RELEASE_DATE = "release_date";
    public static final String FILM_FIELD_DURATION = "duration";
    public static final String FILM_FIELD_MPA_ID = "mpa_id";

    private static final String ALIAS_MPA_ID = "mpa_id";
    private static final String ALIAS_MPA_NAME = "mpa_name";

    public static final String ALIAS_GENRE_IDS = "genre_ids";
    public static final String ALIAS_GENRE_NAMES = "genre_names";

    public static final String TABLE_LIKES = "likes";
    public static final String LIKES_FIELD_FILM_ID = "film_id";
    public static final String LIKES_FIELD_USER_ID = "user_id";

    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public Collection<Film> getAllFilms() {
//        SELECT f.id, f.name, f.description, f.release_date, f.duration,
//                m.id AS mpa_id, m.name AS mpa_name,
//                ARRAY_AGG(g.id) FILTER (WHERE g.id IS NOT NULL) AS genre_ids,
//                ARRAY_AGG(g.name) FILTER (WHERE g.name IS NOT NULL) AS genre_names
//        FROM FILM AS f
//        LEFT JOIN MPA AS m ON f.MPA_ID = m.ID
//        LEFT JOIN FILM_GENRE AS fg ON f.ID = fg.FILM_ID
//        LEFT JOIN GENRE AS g ON fg.GENRE_ID = g.ID
//        GROUP BY f.id;

        String sql =
                String.format("SELECT f.%s, f.%s, f.%s, f.%s, f.%s, ",
                        FILM_FIELD_ID,
                        FILM_FIELD_NAME,
                        FILM_FIELD_DESCRIPTION,
                        FILM_FIELD_RELEASE_DATE,
                        FILM_FIELD_DURATION) +
                        String.format("m.%s AS %s, m.%s AS %s, ",
                                MpaDbStorage.FIELD_ID,
                                ALIAS_MPA_ID,
                                MpaDbStorage.FIELD_NAME,
                                ALIAS_MPA_NAME) +
                        String.format("ARRAY_AGG(g.%s) FILTER (WHERE g.%s IS NOT NULL) AS %s, ",
                                GenreDbStorage.FIELD_ID,
                                GenreDbStorage.FIELD_ID,
                                ALIAS_GENRE_IDS) +
                        String.format("ARRAY_AGG(g.%s) FILTER (WHERE g.%s IS NOT NULL) AS %s ",
                                GenreDbStorage.FIELD_NAME,
                                GenreDbStorage.FIELD_NAME,
                                ALIAS_GENRE_NAMES) +
                        String.format("FROM %s AS f ", TABLE_FILM) +
                        String.format("LEFT JOIN %s AS m ON m.%s = f.%s ",
                                MpaDbStorage.TABLE_MPA,
                                MpaDbStorage.FIELD_ID,
                                FILM_FIELD_MPA_ID) +
                        String.format("LEFT JOIN %s AS fg ON fg.%s = f.%s ",
                                FilmGenreDbStorage.TABLE_FILM_GENRE,
                                FilmGenreDbStorage.FILM_GENRE_FIELD_FILM_ID,
                                FILM_FIELD_ID) +
                        String.format("LEFT JOIN %s AS g ON g.%s = fg.%s ",
                                GenreDbStorage.TABLE_GENRE,
                                GenreDbStorage.FIELD_ID,
                                FilmGenreDbStorage.FILM_GENRE_FIELD_GENRE_ID) +
                        String.format("GROUP BY f.%s;", FILM_FIELD_ID);
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Optional<Film> getById(long id) {
        String sql =
                String.format("SELECT f.%s, f.%s, f.%s, f.%s, f.%s, ",
                        FILM_FIELD_ID,
                        FILM_FIELD_NAME,
                        FILM_FIELD_DESCRIPTION,
                        FILM_FIELD_RELEASE_DATE,
                        FILM_FIELD_DURATION) +
                        String.format("m.%s AS %s, m.%s AS %s, ",
                                MpaDbStorage.FIELD_ID,
                                ALIAS_MPA_ID,
                                MpaDbStorage.FIELD_NAME,
                                ALIAS_MPA_NAME) +
                        String.format("ARRAY_AGG(g.%s) FILTER (WHERE g.%s IS NOT NULL) AS %s, ",
                                GenreDbStorage.FIELD_ID,
                                GenreDbStorage.FIELD_ID,
                                ALIAS_GENRE_IDS) +
                        String.format("ARRAY_AGG(g.%s) FILTER (WHERE g.%s IS NOT NULL) AS %s ",
                                GenreDbStorage.FIELD_NAME,
                                GenreDbStorage.FIELD_NAME,
                                ALIAS_GENRE_NAMES) +
                        String.format("FROM %s AS f ", TABLE_FILM) +
                        String.format("LEFT JOIN %s AS m ON m.%s = f.%s ",
                                MpaDbStorage.TABLE_MPA,
                                MpaDbStorage.FIELD_ID,
                                FILM_FIELD_MPA_ID) +
                        String.format("LEFT JOIN %s AS fg ON fg.%s = f.%s ",
                                FilmGenreDbStorage.TABLE_FILM_GENRE,
                                FilmGenreDbStorage.FILM_GENRE_FIELD_FILM_ID,
                                FILM_FIELD_ID) +
                        String.format("LEFT JOIN %s AS g ON g.%s = fg.%s ",
                                GenreDbStorage.TABLE_GENRE,
                                GenreDbStorage.FIELD_ID,
                                FilmGenreDbStorage.FILM_GENRE_FIELD_GENRE_ID) +
                        String.format("WHERE f.%s = ? ", FILM_FIELD_ID) +
                        String.format("GROUP BY f.%s;", FILM_FIELD_ID);
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);
        return films.isEmpty() ? Optional.empty() : Optional.of(films.get(0));
    }

    @Override
    public long addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_FILM)
                .usingGeneratedKeyColumns(FILM_FIELD_ID);
        return simpleJdbcInsert.executeAndReturnKey(
                Map.of(FILM_FIELD_NAME, film.getName(),
                        FILM_FIELD_DESCRIPTION, film.getDescription(),
                        FILM_FIELD_RELEASE_DATE, Date.valueOf(film.getReleaseDate()),
                        FILM_FIELD_DURATION, film.getDuration(),
                        FILM_FIELD_MPA_ID, film.getMpa().getId()))
                .longValue();
    }

    @Override
    public boolean updateFilm(Film film) {
        String sql = String.format("UPDATE %s ", TABLE_FILM) +
                String.format("SET %s=?, %s=?, %s=?, %s=?, %s=?;",
                        FILM_FIELD_NAME,
                        FILM_FIELD_DESCRIPTION,
                        FILM_FIELD_RELEASE_DATE,
                        FILM_FIELD_DURATION,
                        FILM_FIELD_MPA_ID);
        return jdbcTemplate.update(sql,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                Objects.nonNull(film.getMpa()) ? film.getMpa().getId() : null) > 0;
    }

    @Override
    public boolean addLike(long filmId, long userId) {
        String sql = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)",
                TABLE_LIKES,
                LIKES_FIELD_FILM_ID,
                LIKES_FIELD_USER_ID);
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    @Override
    public boolean removeLike(long filmId, long userId) {
        String sql = String.format("DELETE FROM %s ", TABLE_LIKES) +
                String.format("WHERE %s=? AND %s=?;", LIKES_FIELD_FILM_ID, LIKES_FIELD_USER_ID);
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    @Override
    public int getLikesCount(long filmId) {
        String sql = String.format("SELECT COUNT(1) AS count FROM %s ", TABLE_LIKES) +
                String.format("WHERE %s=? ", LIKES_FIELD_FILM_ID) +
                String.format("GROUP BY %s;", LIKES_FIELD_FILM_ID);
        List<Integer> result = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getInt("count"),
                filmId);
        return result.isEmpty() ? 0 : result.get(0);
    }

    private static Film makeFilm(ResultSet resultSet) throws SQLException {
        Date releaseDate = resultSet.getDate(FILM_FIELD_RELEASE_DATE);

        Array genreIdsRaw = resultSet.getArray(ALIAS_GENRE_IDS);
        Array genreNamesRaw = resultSet.getArray(ALIAS_GENRE_NAMES);
        List<Genre> genres = new ArrayList<>();
        if (Objects.nonNull(genreIdsRaw)) {
            Object[] genreIds = (Object[]) genreIdsRaw.getArray();
            Object[] genreNames = (Object[]) genreNamesRaw.getArray();
//            genres = new ArrayList<>();
            for (int i = 0; i < genreIds.length; i++) {
                genres.add(new Genre((int) genreIds[i], (String) genreNames[i]));
            }
        }

        int mpaId = resultSet.getInt(ALIAS_MPA_ID);
        String mpaName = resultSet.getString(ALIAS_MPA_NAME);

        return new Film(resultSet.getLong(FILM_FIELD_ID),
                resultSet.getString(FILM_FIELD_NAME),
                resultSet.getString(FILM_FIELD_DESCRIPTION),
                Objects.isNull(releaseDate) ? null : releaseDate.toLocalDate(),
                resultSet.getInt(FILM_FIELD_DURATION),
                mpaId == 0 ? null : new Mpa(mpaId, mpaName),
                genres);
    }
}
