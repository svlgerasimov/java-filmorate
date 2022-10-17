package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GenreDbStorage implements GenreStorage {
    public static final String TABLE_GENRE = "genre";
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genre> getAllGenres() {
        String sql = String.format("SELECT %s, %s FROM %s;", FIELD_ID, FIELD_NAME, TABLE_GENRE);
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Optional<Genre> getGenreById(long id) {
        String sql = String.format("SELECT %s, %s FROM %s WHERE %s = ?;",
                FIELD_ID, FIELD_NAME, TABLE_GENRE, FIELD_ID);
        return jdbcTemplate
                .queryForStream(sql, (rs, rowNum) -> makeGenre(rs), id)
                .findFirst();
    }

//    @Override
//    public Collection<Genre> getGenresByIds(Collection<Genre> genres) {
//        if (Objects.isNull(genres) || genres.isEmpty()) {
//            return List.of();
//        }
//        String sql = String.format("SELECT %s, %s WHERE %s IN (%s);",
//                FIELD_ID,
//                FIELD_NAME,
//                FIELD_ID,
//                genres.stream().map(Genre::getId));
//        return null;
//    }

    private static Genre makeGenre(ResultSet resultSet) throws SQLException {
        return new Genre(resultSet.getInt(FIELD_ID), resultSet.getString(FIELD_NAME));
    }
}
