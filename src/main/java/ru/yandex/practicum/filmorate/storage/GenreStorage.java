package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface GenreStorage {
    Collection<Genre> getAllGenres();
    Optional<Genre> getGenreById(long id);
    Map<Integer, Genre> getGenresByIds(Collection<Integer> genreIds);
}

