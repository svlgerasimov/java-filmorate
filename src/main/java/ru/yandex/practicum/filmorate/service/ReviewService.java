package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DbCreateEntityFaultException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Review addReview(Review review) {
        checkUserExists(review.getUserId());
        checkFilmExists(review.getFilmId());

        long id = reviewStorage.addReview(review);
        review = reviewStorage.getReviewById(id).orElseThrow(() ->
                new DbCreateEntityFaultException(String.format("Review (id=%s) hasn't been added to database", id)));
        log.debug("Add review: {}", review);
        return review;
    }

    public Review updateReview(Review review) {
        long id = review.getReviewId();
        reviewStorage.updateReview(review);
        review = reviewStorage.getReviewById(id).orElseThrow(() ->
                new DbCreateEntityFaultException(String.format("Review (id=%s) hasn't been updated in database", id)));
        log.debug("Update review {}", review);
        return review;
    }

    public void removeReview(long id) {
        reviewStorage.removeReview(id);
        log.debug("Remove Review by id={}", id);
    }

    public Review getReviewById(long id) {
        return reviewStorage.getReviewById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Review id=%s not found", id)));
    }

    public List<Review> getAllReview(long filmId, int count) {
        if (filmId != 0) {
            checkFilmExists(filmId);
        }
        return reviewStorage.getAllReview(filmId, count);
    }


    public void addLikeReview(long reviewId, long userId) {
        checkRevievExists(reviewId);
        checkUserExists(userId);
        log.debug("Add Like to Review id={} by user id={}", reviewId, userId);
        reviewStorage.addLikeReview(reviewId, userId);
    }


    public void addDislikeReview(long reviewId, long userId) {
        checkRevievExists(reviewId);
        checkUserExists(userId);
        log.debug("Add Dislike to Review id={} by user id={}", reviewId, userId);
        reviewStorage.addDislikeReview(reviewId, userId);
    }


    public void deleteLikeReview(long reviewId, long userId) {
        checkRevievExists(reviewId);
        checkUserExists(userId);
        log.debug("Delete Like to Review id={} by user id={}", reviewId, userId);
        reviewStorage.deleteLikeReview(reviewId, userId);
    }


    public void deleteDislikeReview(long reviewId, long userId) {
        checkRevievExists(reviewId);
        checkUserExists(userId);
        log.debug("Delete Dislike to Review id={} by user id={}", reviewId, userId);
        reviewStorage.deleteDislikeReview(reviewId, userId);
    }

    private void checkUserExists(long id) {
        userStorage.getById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("User id=%s not found", id)));
    }

    private void checkFilmExists(long id) {
        filmStorage.getById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Film id=%s not found", id)));
    }

    private void checkRevievExists(long id) {
        reviewStorage.getReviewById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Reviev id=%s not found", id)));
    }
}
