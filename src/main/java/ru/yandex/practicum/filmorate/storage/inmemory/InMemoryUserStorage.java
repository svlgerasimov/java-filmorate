package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final IdGenerator idGenerator;

    @Autowired
    public InMemoryUserStorage(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        int id = idGenerator.getNextId();
        user.setId(id);
        users.put(id, user);
        log.debug("Add user {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        int id = user.getId();
        if (!users.containsKey(id)) {
            String message = String.format("User id=%s not found", id);
            log.warn(message);
            throw new NotFoundException(message);
        }
        users.put(id, user);
        log.debug("Update user {}", user);
        return user;
    }
}
