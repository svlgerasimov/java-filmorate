package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DbCreateEntityFaultException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;
    private final EventService eventService;

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        user = preprocess(user);
        long id = userStorage.addUser(user);
        user = userStorage.getById(id).orElseThrow(() ->
                new DbCreateEntityFaultException(String.format("User (id=%s) hasn't been added to database", id)));
        log.debug("Added user {}", user);
        return user;
    }

    public User updateUser(User user) {
        checkUserExists(user.getId());
        user = preprocess(user);
        userStorage.updateUser(user);
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

    public User getUserById(long id) {
        checkUserExists(id);
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%s not found", id)));
    }

    public void addToFriends(long userId, long friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);
        friendsStorage.addFriend(userId, friendId);
        log.debug("Add friends id={} and id={}", userId, friendId);
        eventService.addEvent(userId, EventType.FRIEND, EventOperation.ADD, friendId);
    }

    public void removeFromFriends(long userId, long friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);
        friendsStorage.removeFriend(userId, friendId);
        log.debug("Remove friends id={} and id={}", userId, friendId);
        eventService.addEvent(userId, EventType.FRIEND, EventOperation.REMOVE, friendId);
    }

    public Collection<User> getFriends(long userId) {
        checkUserExists(userId);
        return friendsStorage.getFriends(userId);
    }

    public Collection<User> getCommonFriends(long userId, long otherId) {
        checkUserExists(userId);
        checkUserExists(otherId);
        return friendsStorage.getCommonFriends(userId, otherId);
    }

    public void removeUser(long userId) {
        checkUserExists(userId);
        userStorage.removeUser(userId);
        log.debug("User id = {} removed", userId);
    }

    private void checkUserExists(long id) {
        userStorage.getById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("User id=%s not found", id)));
    }
}
