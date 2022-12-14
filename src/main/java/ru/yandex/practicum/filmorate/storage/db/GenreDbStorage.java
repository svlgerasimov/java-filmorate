package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        String sql = "SELECT id, name FROM genre;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Optional<Genre> getById(long id) {
        String sql = "SELECT id, name FROM genre WHERE id=?;";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), id);
        return genres.isEmpty() ? Optional.empty() : Optional.of(genres.get(0));
    }

    @Override
    public Map<Integer, Genre> getByIds(List<Integer> genreIds) {
        String sql = "SELECT id, name FROM genre WHERE id IN (" +
                genreIds.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ");";
        Map<Integer, Genre> genres = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            genres.put(id, new Genre(id, name));
        });
        return genres;
    }

    private static Genre makeGenre(ResultSet resultSet) throws SQLException {
        return new Genre(resultSet.getInt("id"), resultSet.getString("name"));
    }
}
