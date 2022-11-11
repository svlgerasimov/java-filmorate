package ru.yandex.practicum.filmorate.storage;

public interface LikesStorage {
    boolean addLike(long filmId, long userId);

    boolean removeLike(long filmId, long userId);

    //public int getFilmLikes(long filmId);
}
