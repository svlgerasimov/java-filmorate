package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FilmGenreDbStorage implements FilmGenreStorage {
    public static final String TABLE_FILM_GENRE = "film_genre";
    public static final String FILM_GENRE_FIELD_FILM_ID = "film_id";
    public static final String FILM_GENRE_FIELD_GENRE_ID = "genre_id";

    private final JdbcTemplate jdbcTemplate;

    public void addFilmGenres(long filmId, Collection<Genre> genres) {
        if (Objects.isNull(genres) || genres.size() < 1) {
            return;
        }
        String sql = String.format("MERGE INTO %s (%s, %s) VALUES %s;",
                TABLE_FILM_GENRE,
                FILM_GENRE_FIELD_FILM_ID,
                FILM_GENRE_FIELD_GENRE_ID,
                genres.stream()
                        .map(genre -> String.format("(%d, %d)", filmId, genre.getId()))
                        .collect(Collectors.joining(", ")));
        jdbcTemplate.update(sql);
    }

    public void deleteFilmGenres(long filmId) {
        String sql = String.format("DELETE FROM %s ", TABLE_FILM_GENRE) +
                String.format("WHERE %s=?;", FILM_GENRE_FIELD_FILM_ID);
        jdbcTemplate.update(sql,filmId);
    }
}
