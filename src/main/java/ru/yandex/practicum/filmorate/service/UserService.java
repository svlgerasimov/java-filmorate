package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.inmemory.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Objects;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        user = preprocess(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        user = preprocess(user);
        return userStorage.updateUser(user);
    }

    private User preprocess(User user) {
        String name = user.getName();
        if (Objects.isNull(name) || name.isBlank()) {
            return user.withName(user.getLogin());
        }
        return user;
    }
}
