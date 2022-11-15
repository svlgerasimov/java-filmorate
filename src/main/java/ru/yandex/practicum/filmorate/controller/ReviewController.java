package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
@Validated
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review add(@Valid @RequestBody Review review) {
        return reviewService.add(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        reviewService.remove(id);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable long id) {
        return reviewService.getById(id);
    }

    @GetMapping
    public List<Review> getAll( //  если фильм не указан то все. Если кол-во не указано то 10.
                                @RequestParam(required = false, defaultValue = "10") @Positive int count,
                                @RequestParam(required = false) @Positive Long filmId) {
        return reviewService.getAll(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}") // пользователь ставит лайк отзыву
    public void addLike(@PathVariable(name = "id") long reviewId, @PathVariable long userId) {
        reviewService.addLike(reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}") // пользователь ставит дизлайк отзыву
    public void addDislike(@PathVariable(name = "id") long reviewId, @PathVariable long userId) {
        reviewService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable(name = "id") long reviewId, @PathVariable long userId) {
        reviewService.deleteLike(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}") // пользователь ставит дизлайк отзыву
    public void deleteDislike(@PathVariable(name = "id") long reviewId, @PathVariable long userId) {
        reviewService.deleteDislike(reviewId, userId);
    }
}
