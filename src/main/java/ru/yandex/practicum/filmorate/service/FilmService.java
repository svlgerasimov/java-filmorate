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


    public List<Film> getAll() {
        Map<Long, List<Genre>> genres = filmGenreStorage.getAllFilmGenres();
        Map<Long, List<Director>> directors = filmDirectorsStorage.getAllFilmDirectors();
        return addFieldsToFilms(filmStorage.getAll(), genres, directors);
    }

    public Film add(Film film) {
        checkMpaExists(film);
        checkGenresExist(film);
        long id = filmStorage.add(film);
        filmGenreStorage.addFilmGenres(id, film.getGenres());
        filmDirectorsStorage.saveFilmDirectors(id, film.getDirectors());
        film = filmStorage.getById(id).orElseThrow(() ->
                        new DbCreateEntityFaultException(
                                String.format("Film (id=%s) hasn't been added to database", id)))
                .withGenres(filmGenreStorage.getGenresByFilmId(id))
                .withDirectors(filmDirectorsStorage.getDirectorsByFilmId(id));
        log.debug("Add film: {}", film);
        return film;
    }

    public Film update(Film film) {
        checkMpaExists(film);
        checkGenresExist(film);
        long id = film.getId();
        if (!filmStorage.update(film)) {
            throw new NotFoundException(String.format("Film id = %s not found." ,film.getId()));
        }
        filmGenreStorage.deleteFilmGenres(id);
        filmGenreStorage.addFilmGenres(id, film.getGenres());
        filmDirectorsStorage.deleteFilmDirectors(id);
        filmDirectorsStorage.saveFilmDirectors(id, film.getDirectors());
        film = filmStorage.getById(id).orElseThrow(() ->
                        new DbCreateEntityFaultException(
                                String.format("Film (id=%s) hasn't been updated in database", id)))
                .withGenres(filmGenreStorage.getGenresByFilmId(id))
                .withDirectors(filmDirectorsStorage.getDirectorsByFilmId(id));
        log.debug("Update film {}", film);
        return film;
    }

    public Film getById(long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Film id=%s not found", id)))
                .withGenres(filmGenreStorage.getGenresByFilmId(id))
                .withDirectors(filmDirectorsStorage.getDirectorsByFilmId(id));
    }

    public void addLike(long filmId, long userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);
        likesStorage.addLike(filmId, userId);
        log.debug("Add like to film id={} by user id={}", filmId, userId);
        eventService.add(userId, EventType.LIKE, EventOperation.ADD, filmId);
    }

    public void removeLike(long filmId, long userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);
        likesStorage.removeLike(filmId, userId);
        log.debug("Remove like from film id={} by user id={}", filmId, userId);
        eventService.add(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
    }

    public List<Film> getMostPopularFilms(Integer count, Long genreId, Integer year) {
        List<Film> films = filmStorage.getMostPopularFilms(count, genreId, year);
        return addFieldsToFilms(films);
    }

    public List<Film> findByDirector(long directorId, String sortBy) {
        directorService.checkDirectorExists(directorId);
        List<Film> directorFilms = filmStorage.getFilmsByDirectorId(directorId);
        try {
            switch (FilmSortBy.valueOf(sortBy.toUpperCase())) {
                case YEAR:
                    directorFilms = directorFilms.stream()
                            .sorted(Comparator.comparingInt(o -> o.getReleaseDate().getYear()))
                            .collect(Collectors.toList());
                    break;
                case LIKES:
                    directorFilms = directorFilms.stream()
                            .sorted(Comparator.comparingInt(Film::getRate))
                            .collect(Collectors.toList());
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Такого запроса нет");
        }
        return addFieldsToFilms(directorFilms);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);
        List<Film> commonFilms = filmStorage.getCommonFilms(userId, friendId);
        return addFieldsToFilms(commonFilms);
    }

    public List<Film> getFilmsLikedByUser(long userId) {
        checkUserExists(userId);
        List<Film> films = filmStorage.getFilmsLikedByUser(userId);
        return addFieldsToFilms(films);
    }

    public void remove(long filmId) {
        if(filmStorage.remove(filmId)) {
            log.debug("Film id = {} removed", filmId);
        } else {
            throw new NotFoundException(String.format("Film id = %s not found", filmId));
        }
    }

    public List<Film> search(String query, boolean searchByName, boolean searchByDirector) {
        List<Film> films = filmStorage.search(query, searchByName, searchByDirector);
        return addFieldsToFilms(films);
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
            mpaStorage.getById(id)
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
        Map<Integer, Genre> genres = genreStorage.getByIds(genreIds);
        for (Integer id : genreIds) {
            if (Objects.isNull(genres.get(id))) {
                throw new NotFoundException(String.format("Genre with id=%s not found", id));
            }
        }
    }

    // Добавление к коллекции фильмов полей со списками жанров и режиссёров
    private List<Film> addFieldsToFilms(List<Film> films) {
        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        Map<Long, List<Genre>> genres = filmGenreStorage.getGenresByFilmIds(filmIds);
        Map<Long, List<Director>> directors = filmDirectorsStorage.getDirectorsByFilmIds(filmIds);
        return addFieldsToFilms(films, genres, directors);
    }

    private List<Film> addFieldsToFilms(List<Film> films,
                                        Map<Long, List<Genre>> genres, Map<Long, List<Director>> directors) {
        return films.stream()
                .map(film -> addFieldsToFilm(film, genres, directors))
                .collect(Collectors.toList());
    }

    private Film addFieldsToFilm(Film film, Map<Long, List<Genre>> genres, Map<Long, List<Director>> directors) {
        long filmId = film.getId();
        return film
                .withGenres(genres.getOrDefault(filmId, List.of()))
                .withDirectors(directors.getOrDefault(filmId, List.of()));
    }
}
