package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage, LikesStorage {

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

    @Override
    public Collection<Film> searchByName(String substring) {
        return null;
    }

    @Override
    public Collection<Film> searchByDirector(String substring) {
        return null;
    }


    @Override
    public long addFilm(Film film) {
        long id = idGenerator.getNextId();
        film = film.withId(id);
        films.put(id, film);
        return id;
    }

    @Override
    public boolean updateFilm(Film film) {
        long id = film.getId();
        if (!films.containsKey(id)) {
            return false;
        }
        films.put(id, film);
        return true;
    }

    @Override
    public Collection<Film> getCommonFilms(long userId, long friendId) {
        return null;
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
    public List<Long> findSimilarUsers(long userId, int limit) {
        return null;
    }

    public int getLikesCount(long filmId) {
        Set<Long> filmLikes = likes.get(filmId);
        return Objects.isNull(filmLikes) ? 0 : filmLikes.size();
    }

    @Override
    public Collection<Film> getMostPopularFilms(int count, Long genreId, Integer year) {
        Comparator<Film> comparator = Comparator.comparingInt(film -> getLikesCount(film.getId()));
        return getAllFilms().stream()
                .sorted(comparator.reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Film> getFilmsLikedByUser(long userId) {
        return null;
    }

    @Override
    public List<Film> getFilmsByDirectorId(long directorId) {
        return null;
    }

    @Override
    public void removeFilm(long filmId) {

    }
}
