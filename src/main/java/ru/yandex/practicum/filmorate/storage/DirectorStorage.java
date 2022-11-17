package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    List<Director> getAll();

    Optional<Director> getById(long id);

    Director add(Director director);

    Optional<Director> update(Director director);

    boolean remove(long id);
}
