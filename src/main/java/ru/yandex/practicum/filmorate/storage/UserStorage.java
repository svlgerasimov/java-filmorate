package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getAllUsers();

    Optional<User> getById(long id);

    long addUser(User user);

    boolean updateUser(User user);

    void removeUser(long userId);
}
