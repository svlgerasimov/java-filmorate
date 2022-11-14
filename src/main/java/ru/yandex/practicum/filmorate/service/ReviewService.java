package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DbCreateEntityFaultException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventService eventService;

    public Review add(Review review) {
        checkUserExists(review.getUserId());
        checkFilmExists(review.getFilmId());

        long id = reviewStorage.add(review);
        review = reviewStorage.getById(id).orElseThrow(() ->
                new DbCreateEntityFaultException(String.format("Review (id=%s) hasn't been added to database", id)));
        log.debug("Add review: {}", review);
        eventService.add(review.getUserId(), EventType.REVIEW, EventOperation.ADD, review.getReviewId());
        return review;
    }

    public Review update(Review review) {
        long id = review.getReviewId();
        reviewStorage.update(review);
        review = reviewStorage.getById(id).orElseThrow(() ->
                new DbCreateEntityFaultException(String.format("Review (id=%s) hasn't been updated in database", id)));
        log.debug("Update review {}", review);
        eventService.add(review.getUserId(), EventType.REVIEW, EventOperation.UPDATE, review.getReviewId());
        return review;
    }

    public void remove(long id) {
        Review review = reviewStorage.getById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Review id=%s not found", id)));
        reviewStorage.remove(id);
        log.debug("Remove Review by id={}", id);
        eventService.add(review.getUserId(), EventType.REVIEW, EventOperation.REMOVE, id);
    }

    public Review getById(long id) {
        return reviewStorage.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Review id=%s not found", id)));
    }

    public List<Review> getAll(Long filmId, int count) {
        if (filmId != null) {
            checkFilmExists(filmId);
        }
        return reviewStorage.getAll(filmId, count);
    }


    public void addLike(long reviewId, long userId) {
        checkReviewExists(reviewId);
        checkUserExists(userId);
        log.debug("Add Like to Review id={} by user id={}", reviewId, userId);
        reviewStorage.addLike(reviewId, userId);
    }


    public void addDislike(long reviewId, long userId) {
        checkReviewExists(reviewId);
        checkUserExists(userId);
        log.debug("Add Dislike to Review id={} by user id={}", reviewId, userId);
        reviewStorage.addDislike(reviewId, userId);
    }


    public void deleteLike(long reviewId, long userId) {
        checkReviewExists(reviewId);
        checkUserExists(userId);
        log.debug("Delete Like to Review id={} by user id={}", reviewId, userId);
        reviewStorage.deleteLike(reviewId, userId);
    }


    public void deleteDislike(long reviewId, long userId) {
        checkReviewExists(reviewId);
        checkUserExists(userId);
        log.debug("Delete Dislike to Review id={} by user id={}", reviewId, userId);
        reviewStorage.deleteDislike(reviewId, userId);
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

    private void checkReviewExists(long id) {
        reviewStorage.getById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Review id=%s not found", id)));
    }
}
