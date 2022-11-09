package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.db.DirectorDbStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DirectorController {

    private final DirectorDbStorage directorDbStorage;

    @GetMapping()
    public List<Director> getAllDirectors() {
        return directorDbStorage.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable long id) {
        return directorDbStorage.getDirectorById(id).orElseThrow(() ->
                new NotFoundException("Director not found"));
    }

    @PostMapping
    public Optional<Director> addDirector(@RequestBody @Valid Director director) {
        return directorDbStorage.addDirector(director);
    }

    @PutMapping
    public Optional<Director> updateDirector(@RequestBody Director director) {
        directorDbStorage.getDirectorById(director.getId()).orElseThrow(() ->
                new NotFoundException("Director not found"));
        return directorDbStorage.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") long id) {
        directorDbStorage.getDirectorById(id).orElseThrow(() ->
                new NotFoundException("Director not found"));
        directorDbStorage.deleteDirector(id);
    }
}
