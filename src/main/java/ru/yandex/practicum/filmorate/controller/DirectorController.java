package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.db.DirectorDbStorage;

import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorDbStorage directorDbStorage;

    public DirectorController(DirectorDbStorage directorDbStorage) {
        this.directorDbStorage = directorDbStorage;
    }

    @GetMapping()
    public List<Director> getAllDirectors(){
        return directorDbStorage.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable long id){
        return directorDbStorage.getDirectorById(id);
    }

    @PostMapping
    public Director addDirector(@RequestBody Director director){
        return directorDbStorage.addDirector(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director){
        return directorDbStorage.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") long id){
        directorDbStorage.deleteDirector(id);
    }
}
