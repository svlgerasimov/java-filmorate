package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface FilmGenreStorage {
    void addFilmGenres(long filmId, Collection<Genre> genres);
    void deleteFilmGenres(long filmId);
}
