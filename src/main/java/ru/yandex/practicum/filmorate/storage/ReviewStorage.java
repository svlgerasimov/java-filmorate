package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    long addReview(Review review);

    boolean updateReview(Review review);

    boolean removeReview(long id);

    Optional<Review> getReviewById(long id);

    List<Review> getAllReview(Long filmId, int count);

    boolean addLikeReview(long reviewId, long userId);

    boolean addDislikeReview(long reviewId, long userId);

    boolean deleteLikeReview(long reviewId, long userId);

    boolean deleteDislikeReview(long reviewId, long userId);
}
