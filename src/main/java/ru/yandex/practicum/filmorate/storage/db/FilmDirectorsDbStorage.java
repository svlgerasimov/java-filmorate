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
import ru.yandex.practicum.filmorate.storage.inmemory.FilmDirectorsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FilmDirectorsDbStorage implements FilmDirectorsStorage {

    private final JdbcTemplate jdbcTemplate;

    private final FilmDbStorage filmDbStorage;

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
       // List<Director> directors = film.getDirectors();
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
    public List<Film> findByDirector(long directorId, FilmSortBy sortBy){

        List<Film> directorFilms = filmDbStorage.getFilmsByDirectorId(directorId);
        if(FilmSortBy.year == sortBy){
            return directorFilms.stream().sorted(Comparator.comparingInt(o -> o.getReleaseDate().getYear()))
                    .collect(Collectors.toList());
        } else if(FilmSortBy.likes.equals(sortBy)){
            return directorFilms.stream().sorted(Comparator.comparingInt(o -> o.getRate()))
                    .collect(Collectors.toList());
        } else{
            throw new NotFoundException("Такого запроса нет");
        }
    }
}
