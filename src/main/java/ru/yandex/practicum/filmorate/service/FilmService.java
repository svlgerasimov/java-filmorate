package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage) {
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
        checkFilmExists(film.getId());
        film = filmStorage.updateFilm(film);
        log.debug("Update film {}", film);
        return film;
    }

    public Film getFilmById(long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Film id=%s not found", id)));
    }

    public void addLike(long filmId, long userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);
        filmStorage.addLike(filmId, userId);
        log.debug("Add like to film id={} by user id={}", filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);
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

    private void checkFilmExists(long id) {
        filmStorage.getById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Film id=%s not found", id)));
    }

    private void checkUserExists(long id) {
        userStorage.getById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("User id=%s not found", id)));
    }
}
