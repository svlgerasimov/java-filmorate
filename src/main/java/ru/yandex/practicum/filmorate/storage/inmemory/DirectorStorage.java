package ru.yandex.practicum.filmorate.storage.inmemory;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {

    List<Director> getAllDirectors();

    Director getDirectorById(long id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(long id);
}
