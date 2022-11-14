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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable long id) {
        return filmService.getById(id);
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable(name = "id") long filmId, @PathVariable long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable(name = "id") long filmId, @PathVariable long userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(
            @RequestParam(required = false, defaultValue = "10") @Positive int count,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer year) {
        return filmService.getMostPopularFilms(count, genreId, year);
    }

    @DeleteMapping("/{filmId}")
    public void remove(@PathVariable long filmId) {
        filmService.remove(filmId);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(name = "userId") long userId,
                                           @RequestParam(name = "friendId") long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> findByDirector(@PathVariable long directorId, @RequestParam String sortBy) {
        return filmService.findByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam String query, @RequestParam List<String> by) {
        return filmService.search(query, by.contains("title"), by.contains("director"));
    }
}
