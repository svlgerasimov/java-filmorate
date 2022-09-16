package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private final IdGenerator idGenerator;

    @Autowired
    public UserController(final IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        preprocess(user);
        int id = idGenerator.getNextId();
        user.setId(id);
        users.put(id, user);
        log.debug("Add user {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        preprocess(user);
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

    private void preprocess(User user) {
        String name = user.getName();
        if (Objects.isNull(name) || name.isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
