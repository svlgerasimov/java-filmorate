package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortBy;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Collection<Film> getMostPopularFilms(int count);

    Optional<Film> getById(long id);

    long addFilm(Film film);

    boolean updateFilm(Film film);

    //List<Film> findByDirector(long directorId, FilmSortBy sortBy)
}
