package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAll();

    List<Film> getMostPopularFilms(int count, Long genreId, Integer year);

    List<Film> getFilmsLikedByUser(long userId);

    Optional<Film> getById(long id);

    List<Film> searchByName(String substring);

    List<Film> searchByDirector(String substring);

    long add(Film film);

    boolean update(Film film);

    List<Film> getFilmsByDirectorId(long directorId);

    List<Film> getCommonFilms(long userId, long friendId);

    boolean remove(long filmId);
}
