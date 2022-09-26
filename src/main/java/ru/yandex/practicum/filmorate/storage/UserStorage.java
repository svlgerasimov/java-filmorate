package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Stream;

public interface UserStorage {
    Collection<User> getAllUsers();

    User getById(long id);

    void checkUserExists(long id);

    User addUser(@Valid @RequestBody User user);

    User updateUser(User user);

    boolean addFriend(long userId, long friendId);

    boolean removeFriend(long userId, long friendId);

    Stream<Long> getFriends(long userId);
}
