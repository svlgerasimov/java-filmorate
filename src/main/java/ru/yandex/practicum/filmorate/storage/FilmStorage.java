package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Stream;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film getById(long id);

    void checkFilmExists(long id);

    Film addFilm(Film film);

    Film updateFilm(@Valid @RequestBody Film film);

    boolean addLike(long filmId, long userId);

    boolean removeLike(long filmId, long userId);

    int getLikesCount(long filmId);
}
