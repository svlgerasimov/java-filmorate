package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Qualifier("InMemoryUserStorage")
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    // Если хранить список друзей в User, придётся его "перекладывать" в новый экземпляр при обновлении
    private final Map<Long, Set<Long>> friends = new HashMap<>();
    private final IdGenerator idGenerator;

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public Optional<User> getById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public long addUser(User user) {
        long id = idGenerator.getNextId();
        user = user.withId(id);
        users.put(id, user);
        return id;
    }

    @Override
    public boolean updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            return false;
        }
        users.put(user.getId(), user);
        return true;
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
    public Collection<User> getFriends(long userId) {
        Set<Long> userFriends = friends.get(userId);
        return Objects.isNull(userFriends) ? List.of() :
                userFriends.stream()
                        .map(users::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long otherId) {
        Set<Long> other = friends.get(otherId);
        return Objects.isNull(other) ? List.of() :
                friends.get(userId).stream()
                        .filter(other::contains)
                        .map(users::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
    }
}
