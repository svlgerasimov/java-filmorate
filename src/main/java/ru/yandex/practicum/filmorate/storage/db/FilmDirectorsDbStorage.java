package ru.yandex.practicum.filmorate.storage.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortBy;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmDirectorsStorage;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FilmDirectorsDbStorage implements FilmDirectorsStorage {

    private final JdbcTemplate jdbcTemplate;

    private final FilmDbStorage filmDbStorage;

    private final FilmGenreStorage filmGenreStorage;

    private final DirectorStorage directorStorage;

    @Override
    public void saveFilmDirectors(long filmId, List<Director> directors) {

        if (Objects.isNull(directors) || directors.size() < 1) {
            return;
        }
        String sql = "MERGE INTO film_directors (film_id, director_id) VALUES " +
                directors.stream()
                        .map(director -> String.format("(%d, %d)", filmId, director.getId()))
                        .collect(Collectors.joining(", ")) + ";";
        jdbcTemplate.update(sql);
    }

    @Override
    public List<Director> getDirectorsByFilmId(long filmId) {
        String sql = "SELECT d.director_id, d.name " +
                "FROM director AS d " +
                "JOIN film_directors AS fd ON fd.director_id = d.director_id " +
                "WHERE fd.film_id=?;";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Director(rs.getInt("director_id"), rs.getString("name")),
                filmId);
    }

    @Override
    public void deleteFilmDirectors(long filmId) {
        List<Director> directors = getDirectorsByFilmId(filmId);
        if (Objects.isNull(directors) || directors.size() < 1) {
            return;
        } else {
            String sql = "DELETE FROM film_directors WHERE film_id=?;";
            jdbcTemplate.update(sql, filmId);
        }
    }

    @Override
    public Map<Long, List<Director>> getAllFilmDirectors() {
        String sql = "SELECT f.id AS film_id, d.director_id, d.name " +
                "FROM film AS f " +
                "JOIN film_directors AS fd ON fd.film_id=f.id " +
                "JOIN director AS d ON d.director_id=fd.director_id";
        FilmDirectorsDbStorage.RowCollectorToMap collector = new FilmDirectorsDbStorage.RowCollectorToMap();
        jdbcTemplate.query(sql, collector);
        return collector.getDirectorsByFilm();
    }

    private static class RowCollectorToMap implements RowCallbackHandler {

        @Getter
        private final Map<Long, List<Director>> directorsByFilm = new HashMap<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            long filmId = rs.getLong("film_id");
            List<Director> directors = directorsByFilm.get(filmId);
            if (Objects.isNull(directors)) {
                directors = new ArrayList<>();
                directorsByFilm.put(filmId, directors);
            }
            directors.add(new Director(rs.getInt("director_id"), rs.getString("name")));
        }
    }

    @Override
    public List<Film> findByDirector(long directorId, FilmSortBy sortBy) {
        directorStorage.getDirectorById(directorId);
        List<Film> directorFilms = getFilmsByDirectorId(directorId);
        if (FilmSortBy.year == sortBy) {
            return directorFilms.stream().sorted(Comparator.comparingInt(o -> o.getReleaseDate().getYear()))
                    .collect(Collectors.toList());
        } else if (FilmSortBy.likes.equals(sortBy)) {
            return directorFilms.stream().sorted(Comparator.comparingInt(o -> o.getRate()))
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Такого запроса нет");
        }
    }

    public List<Film> getFilmsByDirectorId(long directorId) {

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
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> filmDbStorage.makeFilm(rs), directorId);
        Map<Long, List<Genre>> genres = filmGenreStorage.getAllFilmGenres();
        Map<Long, List<Director>> directors = getAllFilmDirectors();
        return films.stream().map(film -> film.withGenres(
                        genres.containsKey(film.getId()) ? genres.get(film.getId()) : List.of()))
                .map(film -> film.withDirectors(
                        directors.containsKey(film.getId()) ? directors.get(film.getId())
                                : List.of())).collect(Collectors.toList());
    }
}
