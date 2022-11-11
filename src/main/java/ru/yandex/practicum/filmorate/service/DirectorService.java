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

    public Director addDirector(Director director) {
        return directorStorage.addDirector(director).get();
    }

    public Director getDirectorById(long id) {
        return directorStorage.getDirectorById(id).orElseThrow(() ->
                new NotFoundException("Director not found"));
    }

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director updateDirector(Director director) {
        checkDirectorExists(director.getId());
        return directorStorage.updateDirector(director).orElseThrow(() ->
                new NotFoundException("Director not found"));
    }

    public void deleteDirector(long id) {
        checkDirectorExists(id);
        directorStorage.deleteDirector(id);
    }

    public void checkDirectorExists(long id) {
        directorStorage.getDirectorById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Director id=%s not found", id)));
    }
}
