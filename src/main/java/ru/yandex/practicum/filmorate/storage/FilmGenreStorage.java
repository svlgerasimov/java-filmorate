package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FilmGenreStorage {
    void addFilmGenres(long filmId, Collection<Genre> genres);
    void deleteFilmGenres(long filmId);
    Collection<Genre> getGenresByFilmId(long filmId);
    Map<Long, List<Genre>> getAllFilmGenres();
    Map<Long, List<Genre>> getGenresByFilmIds(Collection<Long> filmIds);
}
