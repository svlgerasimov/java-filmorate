package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Collection<Film> getMostPopularFilms(int count, Long genreId, Integer year);

    Optional<Film> getById(long id);

    long addFilm(Film film);

    boolean updateFilm(Film film);
}
