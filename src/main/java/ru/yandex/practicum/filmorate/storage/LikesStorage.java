package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface LikesStorage {
    boolean addLike(long filmId, long userId);

    boolean removeLike(long filmId, long userId);

    List<Long> findSimilarUsers(long userId, int limit);
}
