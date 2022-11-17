package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DbCreateEntityFaultException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;
    private final EventService eventService;

    public List<User> getAll() {
        return userStorage.getUsers();
    }

    public User add(User user) {
        user = preprocess(user);
        long id = userStorage.add(user);
        user = userStorage.getById(id).orElseThrow(() ->
                new DbCreateEntityFaultException(String.format("User (id=%s) hasn't been added to database", id)));
        log.debug("Added user {}", user);
        return user;
    }

    public User update(User user) {
        user = preprocess(user);
        if (!userStorage.update(user)) {
            throw new NotFoundException(String.format("User id = %s not found", user.getId()));
        }
        long id = user.getId();
        user = userStorage.getById(id).orElseThrow(() ->
                new DbCreateEntityFaultException(String.format("User (id=%s) hasn't been updated in database", id)));
        log.debug("Updated user {}", user);
        return user;
    }

    private User preprocess(User user) {
        String name = user.getName();
        if (Objects.isNull(name) || name.isBlank()) {
            return user.withName(user.getLogin());
        }
        return user;
    }

    public User getById(long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%s not found", id)));
    }

    public void addToFriends(long userId, long friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);
        friendsStorage.addFriend(userId, friendId);
        log.debug("Add friends id={} and id={}", userId, friendId);
        eventService.add(userId, EventType.FRIEND, EventOperation.ADD, friendId);
    }

    public void removeFromFriends(long userId, long friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);
        friendsStorage.removeFriend(userId, friendId);
        log.debug("Remove friends id={} and id={}", userId, friendId);
        eventService.add(userId, EventType.FRIEND, EventOperation.REMOVE, friendId);
    }

    public List<User> getFriends(long userId) {
        checkUserExists(userId);
        return friendsStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        checkUserExists(userId);
        checkUserExists(otherId);
        return friendsStorage.getCommonFriends(userId, otherId);
    }

    public void remove(long userId) {
        if (userStorage.remove(userId)) {
            log.debug("User id = {} removed", userId);
        } else {
            throw new NotFoundException((String.format("User id = %s not found", userId)));
        }
    }

    private void checkUserExists(long id) {
        userStorage.getById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("User id=%s not found", id)));
    }
}
