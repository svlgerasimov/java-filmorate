package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DbCreateEntityFaultException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;

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
    private final EventService eventService;
    private final FilmDirectorsStorage filmDirectorsStorage;

    private final DirectorService directorService;


    public Collection<Film> getAllFilms() {
        Map<Long, List<Genre>> genres = filmGenreStorage.getAllFilmGenres();
        Map<Long, List<Director>> directors = filmDirectorsStorage.getAllFilmDirectors();
        return filmStorage.getAllFilms().stream()
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
        film = filmStorage.getById(id).orElseThrow(() ->
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

    public List<Film> findByDirector(long directorId, String sortBy) {
        directorService.checkDirectorExists(directorId);
        List<Film> sortedFilms;
        List<Film> directorFilms = filmStorage.getFilmsByDirectorId(directorId);
        if (FilmSortBy.YEAR.equals(FilmSortBy.valueOf(sortBy.toUpperCase()))) {
             sortedFilms = directorFilms.stream().sorted(Comparator.comparingInt(o -> o.getReleaseDate().getYear()))
                    .collect(Collectors.toList());
        } else if (FilmSortBy.LIKES.equals(FilmSortBy.valueOf(sortBy.toUpperCase()))) {
             sortedFilms =  directorFilms.stream().sorted(Comparator.comparingInt(o -> o.getRate()))
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Такого запроса нет");
        }
        Map<Long, List<Director>> directors = filmDirectorsStorage.getDirectorsByFilmIds(sortedFilms.stream()
                .map(Film::getId)
                .collect(Collectors.toList()));
        Map<Long, List<Genre>> genres = filmGenreStorage.getGenresByFilmIds(sortedFilms.stream()
                .map(Film::getId)
                .collect(Collectors.toList()));
        return sortedFilms.stream()
                .map(film -> film.withGenres(
                        genres.containsKey(film.getId()) ? genres.get(film.getId()) : List.of()))
                .map(film -> film.withDirectors(
                        directors.containsKey(film.getId()) ? directors.get(film.getId())
                                : List.of())).collect(Collectors.toList());
    }

    public Collection<Film> getCommonFilms(long userId, long friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);
        Map<Long, List<Genre>> genres = filmGenreStorage.getAllFilmGenres();
        Collection<Film> commonFilms = filmStorage.getCommonFilms(userId, friendId);
        return commonFilms.stream()
                .map(film -> film.withGenres(genres.get(film.getId())))
                .collect(Collectors.toList());
    }

    public Collection<Film> getFilmsLikedByUser(long userId) {
        checkUserExists(userId);
        Collection<Film>films = filmStorage.getFilmsLikedByUser(userId);
        Map<Long, List<Genre>> genres = filmGenreStorage.getGenresByFilmIds(films.stream().distinct()
                .map(Film::getId)
                .collect(Collectors.toList()));
        //TODO не забыть добавить режиссёров!!!
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
