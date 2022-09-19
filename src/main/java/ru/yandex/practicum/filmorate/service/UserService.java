package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.inmemory.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
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
        userStorage.checkUserExists(user.getId());
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

    public User getUserById(long id) {
        userStorage.checkUserExists(id);
        return userStorage.getById(id);
    }

    public void addToFriends(long userId, long friendId) {
        userStorage.checkUserExists(userId);
        userStorage.checkUserExists(friendId);
        userStorage.addFriend(userId, friendId);
        userStorage.addFriend(friendId, userId);
    }

    public void removeFromFriends(long userId, long friendId) {
        userStorage.checkUserExists(userId);
        userStorage.checkUserExists(friendId);
        userStorage.removeFriend(userId, friendId);
        userStorage.removeFriend(friendId, userId);
    }

    public Collection<User> getFriends(long userId) {
        userStorage.checkUserExists(userId);
        return userStorage.getFriends(userId)
                .map(userStorage::getById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(long userId, long otherId) {
        userStorage.checkUserExists(userId);
        userStorage.checkUserExists(otherId);
        HashSet<Long> other = userStorage.getFriends(otherId)
                .collect(Collectors.toCollection(HashSet::new));
        return userStorage.getFriends(userId)
                .filter(other::contains)
                .map(userStorage::getById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
