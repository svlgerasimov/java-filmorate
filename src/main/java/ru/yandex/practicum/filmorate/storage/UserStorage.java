package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getUsers();

    Optional<User> getById(long id);

    long add(User user);

    boolean update(User user);

    void remove(long userId);
}
