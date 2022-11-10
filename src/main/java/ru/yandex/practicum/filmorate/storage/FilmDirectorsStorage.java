package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortBy;

import java.util.List;
import java.util.Map;

public interface FilmDirectorsStorage {
    void saveFilmDirectors(long filmId, List<Director> directors);

    List<Director> getDirectorsByFilmId(long filmId);

    void deleteFilmDirectors(long filmId);

    Map<Long, List<Director>> getAllFilmDirectors();

    List<Film> findByDirector(long directorId, FilmSortBy sortBy);
}
