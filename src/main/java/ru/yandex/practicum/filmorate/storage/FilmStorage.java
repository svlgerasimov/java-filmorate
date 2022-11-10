package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Collection<Film> getMostPopularFilms(int count);

    Optional<Film> getById(long id);

    long addFilm(Film film);

    boolean updateFilm(Film film);


//    interface FilmDirectorsStorage {
//
//        void saveFilmDirectors(long filmId, List<Director> directors);
//
//        List<Director> getDirectorsByFilmId(long filmId);
//
//        void deleteFilmDirectors(long filmId);
//
//         Map<Long, List<Director>> getAllFilmDirectors();
//
//         List<Film> findByDirector(long directorId, FilmSortBy sortBy);
//    }
}
