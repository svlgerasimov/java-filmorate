package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.util.*;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> likes = new HashMap<>();
    private final IdGenerator idGenerator;

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Optional<Film> getById(long id) {
        return Optional.ofNullable(films.get(id));
    }

//    @Override
//    public void checkFilmExists(long id) {
//        if (!films.containsKey(id)) {
//            String message = String.format("Film id=%s not found", id);
//            throw new NotFoundException(message);
//        }
//    }

    @Override
    public Film addFilm(Film film) {
        long id = idGenerator.getNextId();
        film = film.withId(id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        long id = film.getId();
        films.put(id, film);
        return film;
    }

    @Override
    public boolean addLike(long filmId, long userId) {
        Set<Long> filmLikes = likes.get(filmId);
        if (Objects.isNull(filmLikes)) {
            filmLikes = new HashSet<>();
            likes.put(filmId, filmLikes);
        }
        return filmLikes.add(userId);
    }

    @Override
    public boolean removeLike(long filmId, long userId) {
        Set<Long> filmLikes = likes.get(filmId);
        if (Objects.isNull(filmLikes)) {
            return false;
        }
        boolean result = filmLikes.remove(userId);
        if (filmLikes.isEmpty()) {
            likes.remove(filmId);
        }
        return result;
    }

    @Override
    public int getLikesCount(long filmId) {
        Set<Long> filmLikes = likes.get(filmId);
        return Objects.isNull(filmLikes) ? 0 : filmLikes.size();
    }
}
