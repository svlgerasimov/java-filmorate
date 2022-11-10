package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping()
    public List<Director> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable long id) {
        return directorService.getDirectorById(id).orElseThrow(() ->
                new NotFoundException("Director not found"));
    }

    @PostMapping
    public Optional<Director> addDirector(@RequestBody @Valid Director director) {
        return directorService.addDirector(director);
    }

    @PutMapping
    public Optional<Director> updateDirector(@RequestBody @Valid Director director) {
        directorService.getDirectorById(director.getId()).orElseThrow(() ->
                new NotFoundException("Director not found"));
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") long id) {
        directorService.getDirectorById(id).orElseThrow(() ->
                new NotFoundException("Director not found"));
        directorService.deleteDirector(id);
    }
}
