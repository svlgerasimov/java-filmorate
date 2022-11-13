package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    List<Director> getAllDirectors();

    Optional<Director> getDirectorById(long id);

    Optional<Director> addDirector(Director director);

    Optional<Director> updateDirector(Director director);

    void deleteDirector(long id);
}
