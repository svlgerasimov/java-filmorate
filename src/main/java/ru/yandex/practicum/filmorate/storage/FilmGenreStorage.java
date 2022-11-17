package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;

public interface FilmGenreStorage {
    void addFilmGenres(long filmId, List<Genre> genres);

    void deleteFilmGenres(long filmId);

    List<Genre> getGenresByFilmId(long filmId);

    Map<Long, List<Genre>> getAllFilmGenres();

    Map<Long, List<Genre>> getGenresByFilmIds(List<Long> filmIds);
}
