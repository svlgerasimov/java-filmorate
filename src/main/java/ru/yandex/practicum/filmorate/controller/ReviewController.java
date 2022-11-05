package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReviewController {

    private final ReviewStorage reviewsStorage;

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        return reviewsStorage.addReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        return reviewsStorage.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
        reviewsStorage.removeReview(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable long id) {
        return reviewsStorage.getReviewById(id);
    }

    @GetMapping
    public List<Review> getAllReview( //  если фильм не указан то все. Если кол-во не указано то 10.
                                      @RequestParam(required = false, defaultValue = "10") @Positive int count,
                                      @RequestParam(required = false, defaultValue = "0") @Positive int filmId) {
        return reviewsStorage.getAllReview(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}") // пользователь ставит лайк отзыву
    public Review addLikeReview(@PathVariable(name = "id") long reviewId, @PathVariable long userId) {
        return reviewsStorage.addLikeReview(reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}") // пользователь ставит дизлайк отзыву
    public Review addDislikeReview(@PathVariable(name = "id") long reviewId, @PathVariable long userId) {
        return reviewsStorage.addDislikeReview(reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review deleteLikeReview(@PathVariable(name = "id") long reviewId, @PathVariable long userId) {
        return reviewsStorage.deleteLikeReview(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}") // пользователь ставит дизлайк отзыву
    public Review deleteDislikeReview(@PathVariable(name = "id") long reviewId, @PathVariable long userId) {
        return reviewsStorage.deleteDislikeReview(reviewId, userId);
    }
}
