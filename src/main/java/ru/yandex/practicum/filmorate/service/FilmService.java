package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.inmemory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.inmemory.InMemoryUserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(final InMemoryFilmStorage filmStorage, final InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        filmStorage.checkFilmExists(film.getId());
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(long id) {
        filmStorage.checkFilmExists(id);
        return filmStorage.getById(id);
    }

    public void addLike(long filmId, long userId) {
        filmStorage.checkFilmExists(filmId);
        userStorage.checkUserExists(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        filmStorage.checkFilmExists(filmId);
        userStorage.checkUserExists(userId);
        filmStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getMostPopularFilms(int count) {
        Comparator<Film> comparator = Comparator.comparingInt(film -> filmStorage.getLikesCount(film.getId()));
        return filmStorage.getAllFilms().stream()
                .sorted(comparator.reversed())
                .limit(count)
                .collect(Collectors.toList());
    }


}
