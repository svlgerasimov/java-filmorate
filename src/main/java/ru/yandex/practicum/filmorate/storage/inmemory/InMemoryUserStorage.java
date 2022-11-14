package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage, FriendsStorage {

    private final Map<Long, User> users = new HashMap<>();
    // Если хранить список друзей в User, придётся его "перекладывать" в новый экземпляр при обновлении
    private final Map<Long, Set<Long>> friends = new HashMap<>();
    private final IdGenerator idGenerator;

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public Optional<User> getById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public long add(User user) {
        long id = idGenerator.getNextId();
        user = user.withId(id);
        users.put(id, user);
        return id;
    }

    @Override
    public boolean update(User user) {
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

    @Override
    public void remove(long userId) {

    }
}
