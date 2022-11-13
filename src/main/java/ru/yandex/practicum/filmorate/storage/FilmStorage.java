package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Collection<Film> getMostPopularFilms(int count, Long genreId, Integer year);

    Optional<Film> getById(long id);

    Collection<Film> searchByName(String substring);

    Collection<Film> searchByDirector(String substring);

    long addFilm(Film film);

    boolean updateFilm(Film film);

    List<Film> getFilmsByDirectorId(long directorId);

    Collection<Film> getCommonFilms(long userId, long friendId);

    void removeFilm(long filmId);
}
