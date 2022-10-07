package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GenreDpStorage implements GenreStorage {
    private static final String fieldId = "id";
    private static final String fieldName = "name";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genre> getAllGenres() {
        String sql = "SELECT id, name FROM genre;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Optional<Genre> getGenreById(long id) {
        String sql = "SELECT id, name FROM genre WHERE id = ?;";
        return jdbcTemplate
                .queryForStream(sql, (rs, rowNum) -> makeGenre(rs), id)
                .findFirst();
    }

    private static Genre makeGenre(ResultSet resultSet) throws SQLException {
        return new Genre(resultSet.getLong(fieldId), resultSet.getString(fieldName));
    }
}
