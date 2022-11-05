package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReviewsDbStorage implements ReviewStorage {


    @Override
    public Review addReview(Review review) {
        return null;
    }

    @Override
    public Review updateReview(Review review) {
        return null;
    }

    @Override
    public void removeReview(long id) {

    }

    @Override
    public Review getReviewById(long id) {
        return null;
    }

    @Override
    public List<Review> getAllReview(long filmId, int count) {
        return null;
    }

    @Override
    public Review addLikeReview(long reviewId, long userId) {
        return null;
    }

    @Override
    public Review addDislikeReview(long reviewId, long userId) {
        return null;
    }

    @Override
    public Review deleteLikeReview(long reviewId, long userId) {
        return null;
    }

    @Override
    public Review deleteDislikeReview(long reviewId, long userId) {
        return null;
    }
}
