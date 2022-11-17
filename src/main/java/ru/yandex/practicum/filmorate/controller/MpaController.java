package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MpaController {
    private final MpaStorage mpaStorage;

    @GetMapping("/{id}")
    public Mpa getById(@PathVariable long id) {
        return mpaStorage.getById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("MPA rating with id=%s not found", id)));
    }

    @GetMapping
    public List<Mpa> getAll() {
        return mpaStorage.getAll();
    }
}
