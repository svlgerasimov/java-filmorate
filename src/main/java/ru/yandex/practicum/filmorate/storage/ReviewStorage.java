package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;
import java.util.List;

public interface ReviewStorage {
    Review addReview(Review review);

    Review updateReview(Review review);

    void removeReview(long id);

    Review getReviewById(long id);

    List<Review> getAllReview(long filmId, int count);

    Review addLikeReview(long reviewId, long userId);

    Review addDislikeReview(long reviewId, long userId);

    Review deleteLikeReview(long reviewId, long userId);

    Review deleteDislikeReview(long reviewId, long userId);
}
