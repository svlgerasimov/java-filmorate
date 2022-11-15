package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortBy;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAll();

    List<Film> getMostPopularFilms(int count, Long genreId, Integer year);

    List<Film> getFilmsLikedByUser(long userId);

    Optional<Film> getById(long id);

    List<Film> search(String substring, boolean searchByName, boolean searchByDirector);

    long add(Film film);

    boolean update(Film film);

    List<Film> getFilmsByDirectorId(long directorId, FilmSortBy sortBy);

    List<Film> getCommonFilms(long userId, long friendId);

    boolean remove(long filmId);
}
