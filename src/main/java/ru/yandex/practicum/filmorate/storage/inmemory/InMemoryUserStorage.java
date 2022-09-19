package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.util.*;
import java.util.stream.Stream;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    // Если хранить список друзей в User, придётся его "перекладывать" в новый экземпляр при обновлении
    private final Map<Long, Set<Long>> friends = new HashMap<>();
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
    public User getById(long id) {
        return users.get(id);
    }

    @Override
    public void checkUserExists(long id) {
        if (!users.containsKey(id)) {
            String message = String.format("User id=%s not found", id);
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public User addUser(User user) {
        long id = idGenerator.getNextId();
        user = user.withId(id);
        users.put(id, user);
        log.debug("Add user {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.debug("Update user {}", user);
        return user;
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        Set<Long> userFriends = friends.get(userId);
        if (Objects.isNull(userFriends)) {
            userFriends = new HashSet<>();
            friends.put(userId, userFriends);
        }
        return userFriends.add(friendId);
    }

    @Override
    public boolean removeFriend(long userId, long friendId) {
        Set<Long> userFriends = friends.get(userId);
        if (Objects.isNull(userFriends)) {
            return false;
        }
        boolean result = userFriends.remove(friendId);
        if (userFriends.isEmpty()) {
            friends.remove(userId);
        }
        return result;
    }

    @Override
    public Stream<Long> getFriends(long userId) {
        Set<Long> userFriends = friends.get(userId);
        return Objects.isNull(userFriends) ? Stream.empty() : userFriends.stream();
    }
}
