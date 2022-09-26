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
import java.util.stream.Collectors;

@Slf4j
@Service
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
        film = filmStorage.addFilm(film);
        log.debug("Add film: {}", film);
        return film;
    }

    public Film updateFilm(Film film) {
        filmStorage.checkFilmExists(film.getId());
        film = filmStorage.updateFilm(film);
        log.debug("Update film {}", film);
        return film;
    }

    public Film getFilmById(long id) {
        filmStorage.checkFilmExists(id);
        return filmStorage.getById(id);
    }

    public void addLike(long filmId, long userId) {
        filmStorage.checkFilmExists(filmId);
        userStorage.checkUserExists(userId);
        filmStorage.addLike(filmId, userId);
        log.debug("Add like to film id={} by user id={}", filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        filmStorage.checkFilmExists(filmId);
        userStorage.checkUserExists(userId);
        filmStorage.removeLike(filmId, userId);
        log.debug("Remove like from film id={} by user id={}", filmId, userId);
    }

    public Collection<Film> getMostPopularFilms(int count) {
        Comparator<Film> comparator = Comparator.comparingInt(film -> filmStorage.getLikesCount(film.getId()));
        return filmStorage.getAllFilms().stream()
                .sorted(comparator.reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
