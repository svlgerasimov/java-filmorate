package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Director add(Director director) {
        return directorStorage.add(director).get();
    }

    public Director getById(long id) {
        return directorStorage.getById(id).orElseThrow(() ->
                new NotFoundException("Director not found"));
    }

    public List<Director> getAll() {
        return directorStorage.getAll();
    }

    public Director update(Director director) {
        checkDirectorExists(director.getId());
        return directorStorage.update(director).orElseThrow(() ->
                new NotFoundException("Director not found"));
    }

    public void remove(long id) {
        checkDirectorExists(id);
        directorStorage.remove(id);
    }

    public void checkDirectorExists(long id) {
        directorStorage.getById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Director id=%s not found", id)));
    }
}
