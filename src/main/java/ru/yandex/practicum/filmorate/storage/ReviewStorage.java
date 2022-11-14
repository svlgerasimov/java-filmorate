package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    long add(Review review);

    boolean update(Review review);

    boolean remove(long id);

    Optional<Review> getById(long id);

    List<Review> getAll(Long filmId, int count);

    boolean addLike(long reviewId, long userId);

    boolean addDislike(long reviewId, long userId);

    boolean deleteLike(long reviewId, long userId);

    boolean deleteDislike(long reviewId, long userId);
}
