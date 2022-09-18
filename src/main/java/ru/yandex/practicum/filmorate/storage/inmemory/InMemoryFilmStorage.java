package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private final IdGenerator idGenerator;

    @Autowired
    public InMemoryFilmStorage(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        int id = idGenerator.getNextId();
        film = film.withId(id);
        films.put(id, film);
        log.debug("Add film: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(@Valid @RequestBody Film film) {
        int id = film.getId();
        if (!films.containsKey(id)) {
            String message = String.format("Film id=%s not found", id);
            log.warn(message);
            throw new NotFoundException(message);
        }
        films.put(id, film);
        log.debug("Update film {}", film);
        return film;
    }
}
