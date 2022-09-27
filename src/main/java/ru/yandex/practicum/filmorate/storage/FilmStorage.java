package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Optional<Film> getById(long id);

    void checkFilmExists(long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    boolean addLike(long filmId, long userId);

    boolean removeLike(long filmId, long userId);

    int getLikesCount(long filmId);
}
