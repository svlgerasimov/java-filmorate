package ru.yandex.practicum.filmorate.storage;

public interface LikesStorage {
    boolean addLike(long filmId, long userId);

    boolean removeLike(long filmId, long userId);

    boolean removeLikesByFilm(long filmId);

    boolean removeLikesByUser(long userId);
}
