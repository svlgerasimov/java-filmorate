package ru.yandex.practicum.filmorate.storage.inmemory;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface FilmDirectorsStorage {

    void saveFilmDirectors(long filmId, ArrayList<Director> directors);

    List<Director> getDirectorsByFilmId(long filmId);

    void deleteFilmDirectors(Film film);

    public Map<Long, ArrayList<Director>> getAllFilmDirectors();

    public List<Film> findByDirector(long directorId, FilmSortBy sortBy);

}
