package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DbCreateEntityFaultException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.inmemory.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.inmemory.FilmDirectorsStorage;

import java.util.*;
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

    private final DirectorStorage directorStorage;

    private final FilmDirectorsStorage filmDirectorsStorage;


    public Collection<Film> getAllFilms() {
        Map<Long, List<Genre>> genres = filmGenreStorage.getAllFilmGenres();
        Map<Long, List<Director>> directors = filmDirectorsStorage.getAllFilmDirectors();
        return  filmStorage.getAllFilms().stream()
                .map(film -> film.withGenres(
                        genres.containsKey(film.getId()) ? genres.get(film.getId()) : List.of()))
                .map(film -> film.withDirectors(
                        directors.containsKey(film.getId()) ? directors.get(film.getId())
                                : List.of())).collect(Collectors.toList());
    }

    public Film addFilm(Film film) {
        checkMpaExists(film);
        checkGenresExist(film);
        long id = filmStorage.addFilm(film);
        filmGenreStorage.addFilmGenres(id, film.getGenres());
        filmDirectorsStorage.saveFilmDirectors(id, film.getDirectors());
        film = filmStorage.getById(id).orElseThrow(() ->
                new DbCreateEntityFaultException(String.format("Film (id=%s) hasn't been added to database", id)))
                .withGenres(filmGenreStorage.getGenresByFilmId(id))
                .withDirectors(filmDirectorsStorage.getDirectorsByFilmId(id));
        log.debug("Add film: {}", film);
        log.debug("Dir: {}", film.getDirectors());
        log.debug("Directors: {}", filmDirectorsStorage.getDirectorsByFilmId(id));
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
        filmDirectorsStorage.deleteFilmDirectors(id);
        filmDirectorsStorage.saveFilmDirectors(id, film.getDirectors());
        film = filmStorage.getById(id).orElseThrow(()  ->
                new DbCreateEntityFaultException(String.format("Film (id=%s) hasn't been updated in database", id)))
                .withGenres(filmGenreStorage.getGenresByFilmId(id))
                .withDirectors(filmDirectorsStorage.getDirectorsByFilmId(id));
        log.debug("Update film {}", film);
        return film;
    }

    public Film getFilmById(long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Film id=%s not found", id)))
                .withGenres(filmGenreStorage.getGenresByFilmId(id)).withDirectors(filmDirectorsStorage.getDirectorsByFilmId(id));
    }

    public void addLike(long filmId, long userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);
        likesStorage.addLike(filmId, userId);
        log.debug("Add like to film id={} by user id={}", filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);
        likesStorage.removeLike(filmId, userId);
        log.debug("Remove like from film id={} by user id={}", filmId, userId);
    }

    public Collection<Film> getMostPopularFilms(int count) {
        Collection<Film> films = filmStorage.getMostPopularFilms(count);
        Map<Long, List<Genre>> genres = filmGenreStorage.getGenresByFilmIds(films.stream()
                .map(Film::getId)
                .collect(Collectors.toList()));
        return films.stream()
                .map(film -> film.withGenres(genres.get(film.getId())))
                .collect(Collectors.toList());
    }

    public List<Film> findByDirector(long directorId, FilmSortBy sortBy){
        return filmDirectorsStorage.findByDirector(directorId, sortBy);
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
