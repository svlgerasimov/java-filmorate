package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GenreController {
    private final GenreStorage genreStorage;

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable long id) {
        return genreStorage.getGenreById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Genre with id=%s not found", id)));
    }

    @GetMapping
    public Collection<Genre> getAllMpa() {
        return genreStorage.getAllGenres();
    }
}
