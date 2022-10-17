package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public interface UserStorage {
    Collection<User> getAllUsers();

    Optional<User> getById(long id);

    long addUser(User user);

    boolean updateUser(User user);

    boolean addFriend(long userId, long friendId);

    boolean removeFriend(long userId, long friendId);

    Collection<User> getFriends(long userId);

    Collection<User> getCommonFriends(long userId, long otherId);
}
