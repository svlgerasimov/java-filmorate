package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Collection<Film> getMostPopularFilms(int count);

    Optional<Film> getById(long id);

    Collection<Film> findByName(String substring);

    Collection<Film> findByDirector(String substring);

    long addFilm(Film film);

    boolean updateFilm(Film film);
}
