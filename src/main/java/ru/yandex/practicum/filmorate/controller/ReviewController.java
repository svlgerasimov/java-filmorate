package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
        reviewService.removeReview(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getAllReview( //  если фильм не указан то все. Если кол-во не указано то 10.
                                      @RequestParam(required = false, defaultValue = "10") @Positive int count,
                                      @RequestParam(required = false, defaultValue = "0") @Positive int filmId) {
        return reviewService.getAllReview(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}") // пользователь ставит лайк отзыву
    public void addLikeReview(@PathVariable(name = "id") long reviewId, @PathVariable long userId) {
        reviewService.addLikeReview(reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}") // пользователь ставит дизлайк отзыву
    public void addDislikeReview(@PathVariable(name = "id") long reviewId, @PathVariable long userId) {
        reviewService.addDislikeReview(reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeReview(@PathVariable(name = "id") long reviewId, @PathVariable long userId) {
        reviewService.deleteLikeReview(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}") // пользователь ставит дизлайк отзыву
    public void deleteDislikeReview(@PathVariable(name = "id") long reviewId, @PathVariable long userId) {
        reviewService.deleteDislikeReview(reviewId, userId);
    }
}
