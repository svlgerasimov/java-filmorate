package ru.yandex.practicum.filmorate.storage.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFilmGenres(long filmId, Collection<Genre> genres) {
        if (Objects.isNull(genres) || genres.size() < 1) {
            return;
        }
        String sql = "MERGE INTO film_genre (film_id, genre_id) VALUES " +
                genres.stream()
                        .map(genre -> String.format("(%d, %d)", filmId, genre.getId()))
                        .collect(Collectors.joining(", ")) + ";";
        jdbcTemplate.update(sql);
    }

    @Override
    public void deleteFilmGenres(long filmId) {
        String sql = "DELETE FROM film_genre WHERE film_id=?;";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public Collection<Genre> getGenresByFilmId(long filmId) {
        String sql = "SELECT g.id, g.name " +
                "FROM film_genre AS fg " +
                "JOIN genre AS g ON g.id=fg.genre_id " +
                "WHERE fg.film_id=?;";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")),
                filmId);
    }

    @Override
    public Map<Long, List<Genre>> getAllFilmGenres() {
        String sql = "SELECT f.id AS film_id, g.id, g.name " +
                "FROM film AS f " +
                "JOIN film_genre AS fg ON fg.film_id=f.id " +
                "JOIN genre AS g ON g.id=fg.genre_id;";
        RowCollectorToMap collector = new RowCollectorToMap();
        jdbcTemplate.query(sql, collector);
        return collector.getGenresByFilm();
    }

    @Override
    public Map<Long, List<Genre>> getGenresByFilmIds(Collection<Long> filmIds) {
        String sql = "SELECT f.id AS film_id, g.id, g.name " +
                "FROM film AS f " +
                "JOIN film_genre AS fg ON fg.film_id=f.id " +
                "JOIN genre AS g ON g.id=fg.genre_id " +
                "WHERE f.id IN (" +
                filmIds.stream().map(String::valueOf).collect(Collectors.joining(",")) +
                ");";
        RowCollectorToMap collector = new RowCollectorToMap();
        jdbcTemplate.query(sql, collector);
        return collector.getGenresByFilm();
    }

    private static class RowCollectorToMap implements RowCallbackHandler {

        @Getter
        private final Map<Long, List<Genre>> genresByFilm = new HashMap<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            long filmId = rs.getLong("film_id");
            List<Genre> genres = genresByFilm.get(filmId);
            if (Objects.isNull(genres)) {
                genres = new ArrayList<>();
                genresByFilm.put(filmId, genres);
            }
            genres.add(new Genre(rs.getInt("id"), rs.getString("name")));
        }
    }
}

