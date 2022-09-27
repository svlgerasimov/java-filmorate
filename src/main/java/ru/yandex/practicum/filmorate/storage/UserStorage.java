package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public interface UserStorage {
    Collection<User> getAllUsers();

    Optional<User> getById(long id);

    void checkUserExists(long id);

    User addUser(User user);

    User updateUser(User user);

    boolean addFriend(long userId, long friendId);

    boolean removeFriend(long userId, long friendId);

    Stream<Long> getFriends(long userId);
}
