package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

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

    public static final String TABLE_FILM_GENRE = "film_genre";
    public static final String FILM_GENRE_FIELD_ID = "id";
    public static final String FILM_GENRE_FIELD_FILM_ID = "film_id";
    public static final String FILM_GENRE_FIELD_GENRE_ID = "genre_id";

    public static final String TABLE_LIKES = "likes";
    public static final String LIKES_FIELD_ID = "id";
    public static final String LIKES_FIELD_FILM_ID = "film_id";
    public static final String LIKES_FIELD_USER_ID = "user_id";

    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public Collection<Film> getAllFilms() {
//        String sql = String.format("SELECT %s, %s, %s, %s, %s FROM %s;",
//                USER_FIELD_ID,
//                USER_FIELD_EMAIL,
//                USER_FIELD_LOGIN,
//                USER_FIELD_NAME,
//                USER_FIELD_BIRTHDAY,
//                TABLE_USER);
//        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));

        String sql =
                String.format("SELECT f.%s, f.%s, f.%s, f.%s, f.%s, ",
                        FILM_FIELD_ID,
                        FILM_FIELD_NAME,
                        FILM_FIELD_DESCRIPTION,
                        FILM_FIELD_RELEASE_DATE,
                        FILM_FIELD_DURATION) +
                        String.format("m.%s AS mpa_id, m.%s AS mpa_name, ",
                                MpaDpStorage.FIELD_ID,
                                MpaDpStorage.FIELD_NAME) +
                        String.format("ARRAY_AGG(g.%s) AS genre_ids, ARRAY_AGG(g.%s) AS genre_name ",
                                GenreDpStorage.FIELD_ID,
                                GenreDpStorage.FIELD_NAME) +
                        String.format("FROM %s AS f ", TABLE_FILM) +
                        String.format("JOIN %s AS m ON m.%s = f.%s ",
                                MpaDpStorage.TABLE_MPA,
                                MpaDpStorage.FIELD_ID,
                                FILM_FIELD_MPA_ID) +
                        String.format("JOIN %s AS fg ON fg.%s = f.%s ",
                                TABLE_FILM_GENRE,
                                FILM_GENRE_FIELD_FILM_ID,
                                FILM_FIELD_ID) +
                        String.format("JOIN %s AS g ON g.%s = fg.%s ",
                                GenreDpStorage.TABLE_GENRE,
                                GenreDpStorage.FIELD_ID,
                                FILM_GENRE_FIELD_GENRE_ID) +
                        String.format("GROUP BY f.%s;", FILM_FIELD_ID);
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Optional<Film> getById(long id) {
//        String sql = String.format("SELECT %s, %s, %s, %s, %s FROM %s WHERE %s = ?;",
//                USER_FIELD_ID,
//                USER_FIELD_EMAIL,
//                USER_FIELD_LOGIN,
//                USER_FIELD_NAME,
//                USER_FIELD_BIRTHDAY,
//                TABLE_USER,
//                USER_FIELD_ID);
//        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
//        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
        return Optional.empty();
    }

    @Override
    public Film addFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public boolean addLike(long filmId, long userId) {
        return false;
    }

    @Override
    public boolean removeLike(long filmId, long userId) {
        return false;
    }

    @Override
    public int getLikesCount(long filmId) {
        return 0;
    }

    private static Film makeFilm(ResultSet resultSet) throws SQLException {
//        return new Film(1, "", "", LocalDate.now(), 180, new Mpa(1, "d"), new ArrayList<>());
        Date releaseDate = resultSet.getDate(FILM_FIELD_RELEASE_DATE);
//        Object o = resultSet.getArray("genre_ids").getArray();
//        Object[] objects = (Object[]) o;
//        log.debug(resultSet.getArray("genre_ids").getArray().getClass().toString());
        Object[] genreIds = (Object[]) resultSet.getArray("genre_ids").getArray();
        Object[] genreNames = (Object[]) resultSet.getArray("genre_name").getArray();
        List<Genre> genres = new ArrayList<>();
        for (int i = 0; i < genreIds.length; i++) {
            genres.add(new Genre((int) genreIds[i], (String) genreNames[i]));
        }
        return new Film(resultSet.getLong(FILM_FIELD_ID),
                resultSet.getString(FILM_FIELD_NAME),
                resultSet.getString(FILM_FIELD_DESCRIPTION),
                Objects.isNull(releaseDate) ? null : releaseDate.toLocalDate(),
                resultSet.getInt(FILM_FIELD_DURATION),
                new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa_name")),
                genres);
    }
}
