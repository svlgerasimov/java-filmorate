package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DbCreateEntityFaultException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.EventOperation;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.EventType;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmService {
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final UserStorage userStorage;
    private final LikesStorage likesStorage;
    private final EventService eventService;

    public Collection<Film> getAllFilms() {
        Map<Long, List<Genre>> genres = filmGenreStorage.getAllFilmGenres();
        return filmStorage.getAllFilms().stream()
                .map(film -> film.withGenres(genres.get(film.getId())))
                .collect(Collectors.toList());
    }

    public Film addFilm(Film film) {
        checkMpaExists(film);
        checkGenresExist(film);
        long id = filmStorage.addFilm(film);
        filmGenreStorage.addFilmGenres(id, film.getGenres());
        film = filmStorage.getById(id).orElseThrow(() ->
                        new DbCreateEntityFaultException(String.format("Film (id=%s) hasn't been added to database", id)))
                .withGenres(filmGenreStorage.getGenresByFilmId(id));
        log.debug("Add film: {}", film);
        return film;
    }

    public Film updateFilm(Film film) {
        checkMpaExists(film);
        checkGenresExist(film);
        long id = film.getId();
        checkFilmExists(id);
        filmStorage.updateFilm(film);
        filmGenreStorage.deleteFilmGenres(id);
        filmGenreStorage.addFilmGenres(id, film.getGenres());
        film = filmStorage.getById(id).orElseThrow(() ->
                        new DbCreateEntityFaultException(String.format("Film (id=%s) hasn't been updated in database", id)))
                .withGenres(filmGenreStorage.getGenresByFilmId(id));
        log.debug("Update film {}", film);
        return film;
    }

    public Film getFilmById(long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Film id=%s not found", id)))
                .withGenres(filmGenreStorage.getGenresByFilmId(id));
    }

    public void addLike(long filmId, long userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);
        likesStorage.addLike(filmId, userId);
        log.debug("Add like to film id={} by user id={}", filmId, userId);
        eventService.addEvent(userId, EventType.LIKE, EventOperation.ADD, filmId);
    }

    public void removeLike(long filmId, long userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);
        likesStorage.removeLike(filmId, userId);
        log.debug("Remove like from film id={} by user id={}", filmId, userId);
        eventService.addEvent(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
    }

    public Collection<Film> getMostPopularFilms(Integer count, Long genreId, Integer year) {
        Collection<Film> films = filmStorage.getMostPopularFilms(count, genreId, year);
        Map<Long, List<Genre>> genres = filmGenreStorage.getGenresByFilmIds(films.stream()
                .map(Film::getId)
                .collect(Collectors.toList()));
        return films.stream()
                .map(film -> film.withGenres(genres.get(film.getId())))
                .collect(Collectors.toList());
    }

    public void removeFilm(long filmId) {
        checkFilmExists(filmId);
        filmStorage.removeFilm(filmId);
        log.debug("Film id = {} removed", filmId);
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

    private void checkMpaExists(Film film) {
        Mpa mpa = film.getMpa();
        if (Objects.nonNull(mpa)) {
            int id = mpa.getId();
            mpaStorage.getMpaById(id)
                    .orElseThrow(() ->
                            new NotFoundException(String.format("Mpa rating with id=%s not found", id)));
        }
    }

    private void checkGenresExist(Film film) {
        if (Objects.isNull(film.getGenres())) {
            return;
        }
        List<Integer> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        Map<Integer, Genre> genres = genreStorage.getGenresByIds(genreIds);
        for (Integer id : genreIds) {
            if (Objects.isNull(genres.get(id))) {
                throw new NotFoundException(String.format("Genre with id=%s not found", id));
            }
        }
    }
}
