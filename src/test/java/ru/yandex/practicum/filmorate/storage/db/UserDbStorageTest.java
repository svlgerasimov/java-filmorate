package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    public void getAllUsersWithNoUsersPresent() {
        Collection<User> users = userStorage.getAllUsers();
        assertThat(users).isEmpty();
    }

    @Test
    public void getUserByIncorrectId() {
        User user = new User(0, "email", "login", "name",
                LocalDate.of(1999, Month.JANUARY, 1));
        long id = userStorage.addUser(user);
        Optional<User> userOptional = userStorage.getById(id + 1);
        assertThat(userOptional).isEmpty();
    }

    @Test
    public void addAndGetValidUser() {
        User user = new User(0, "email", "login", "name",
                LocalDate.of(1999, Month.JANUARY, 1));
        long id = userStorage.addUser(user);

        User userExpected = user.withId(id);
        Optional<User> userOptional = userStorage.getById(id);
        assertThat(userOptional)
                .isPresent()
                .hasValue(userExpected);

        Collection<User> users = userStorage.getAllUsers();
        assertThat(users)
                .isNotEmpty()
                .hasSize(1)
                .contains(userExpected);
    }

    @Test
    public void addUserWithNullEmailAndThenThrowException() {
        User user = new User(0, null, "login", "name",
                LocalDate.of(1999, Month.JANUARY, 1));
        assertThatThrownBy(() -> userStorage.addUser(user))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addUserWithNullLoginAndThenThrowException() {
        User user = new User(0, "email", null, "name",
                LocalDate.of(1999, Month.JANUARY, 1));
        assertThatThrownBy(() -> userStorage.addUser(user))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addUserWithNullNameAndThenThrowException() {
        User user = new User(0, "email", "login", null,
                LocalDate.of(1999, Month.JANUARY, 1));
        assertThatThrownBy(() -> userStorage.addUser(user))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
    
    @Test
    public void updateUserWithCorrectId() {
        User user1 = new User(0, "email1", "login1", "name1",
                LocalDate.of(1999, Month.JANUARY, 1));
        long id1 = userStorage.addUser(user1);
        User user2 = new User(0, "email2", "login2", "name2",
                LocalDate.of(1998, Month.FEBRUARY, 3));
        long id2 = userStorage.addUser(user2);
        user2 = user2.withId(id2);
        user1 = new User(id1, "updated email1", "updated login1", "updated name1",
                LocalDate.of(2000, Month.DECEMBER, 2));
        assertThat(userStorage.updateUser(user1)).isEqualTo(true);
        assertThat(userStorage.getAllUsers())
                .isNotEmpty()
                .hasSize(2)
                .containsAll(List.of(user1, user2));
    }

    @Test
    public void updateUserWithAbsentId() {
        User user = new User(0, "email", "login", "name",
                LocalDate.of(1999, Month.JANUARY, 1));
        long id = userStorage.addUser(user);
        user = user.withId(id);
        User updatedUser = new User(id + 1, "updated email", "updated login", "updated name",
                LocalDate.of(1998, Month.FEBRUARY, 2));
        assertThat(userStorage.updateUser(updatedUser)).isEqualTo(false);
        assertThat(userStorage.getAllUsers())
                .hasSize(1)
                .contains(user);
    }

    @Test
    public void addFriend() {
        User user1 = new User(0, "email1", "login1", "name1",
                LocalDate.of(2001, Month.JANUARY, 1));
        User user2 = new User(0, "email2", "login2", "name2",
                LocalDate.of(2002, Month.FEBRUARY, 2));
        User user3 = new User(0, "email3", "login3", "name3",
                LocalDate.of(2003, Month.MARCH, 3));
        user1 = user1.withId(userStorage.addUser(user1));
        user2 = user2.withId(userStorage.addUser(user2));
        user3 = user3.withId(userStorage.addUser(user3));

        assertThat(userStorage.addFriend(user1.getId(), user2.getId())).isEqualTo(true);
        assertThat(userStorage.addFriend(user1.getId(), user3.getId())).isEqualTo(true);
        assertThat(userStorage.addFriend(user2.getId(), user1.getId())).isEqualTo(true);
        assertThat(userStorage.addFriend(user2.getId(), user3.getId())).isEqualTo(true);

        assertThat(userStorage.getFriends(user1.getId()))
                .isNotEmpty()
                .hasSize(2)
                .containsAll(List.of(user2, user3));
        assertThat(userStorage.getFriends(user2.getId()))
                .isNotEmpty()
                .hasSize(2)
                .containsAll(List.of(user1, user3));
        assertThat(userStorage.getFriends(user3.getId()))
                .isEmpty();
    }

    @Test
    public void addFriendWithAbsentIdAndThenThrowException() {
        User user = new User(0, "email", "login", "name",
                LocalDate.of(2000, Month.JANUARY, 1));
        long id = userStorage.addUser(user);

        assertThatThrownBy(() -> userStorage.addFriend(id, id + 1))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> userStorage.addFriend(id + 1, id))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addFriendTwice() {
        User user1 = new User(0, "email1", "login1", "name1",
                LocalDate.of(2001, Month.JANUARY, 1));
        User user2 = new User(0, "email2", "login2", "name2",
                LocalDate.of(2002, Month.FEBRUARY, 2));
        long id1 = userStorage.addUser(user1);
        long id2 = userStorage.addUser(user2);

        userStorage.addFriend(id1, id2);
        assertThatNoException().isThrownBy(() -> userStorage.addFriend(id1, id2));
        assertThat(userStorage.getFriends(id1)).hasSize(1);
        assertThat(userStorage.getFriends(id2)).isEmpty();
    }

    @Test
    public void addFriendWithSameIdAndThenThrowException() {
        User user = new User(0, "email", "login", "name",
                LocalDate.of(2000, Month.JANUARY, 1));
        long id = userStorage.addUser(user);
        assertThatThrownBy(() -> userStorage.addFriend(id, id))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    public void getCommonFriends() {
        User user1 = new User(0, "email1", "login1", "name1",
                LocalDate.of(2001, Month.JANUARY, 1));
        User user2 = new User(0, "email2", "login2", "name2",
                LocalDate.of(2002, Month.FEBRUARY, 2));
        User user3 = new User(0, "email3", "login3", "name3",
                LocalDate.of(2003, Month.MARCH, 3));
        user1 = user1.withId(userStorage.addUser(user1));
        user2 = user2.withId(userStorage.addUser(user2));
        user3 = user3.withId(userStorage.addUser(user3));

        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.addFriend(user1.getId(), user3.getId());
        userStorage.addFriend(user2.getId(), user1.getId());
        userStorage.addFriend(user2.getId(), user3.getId());

        assertThat(userStorage.getCommonFriends(user1.getId(), user2.getId()))
                .isNotEmpty()
                .hasSize(1)
                .contains(user3);
    }

    @Test
    public void removeFriend() {
        User user1 = new User(0, "email1", "login1", "name1",
                LocalDate.of(2001, Month.JANUARY, 1));
        User user2 = new User(0, "email2", "login2", "name2",
                LocalDate.of(2002, Month.FEBRUARY, 2));
        long id1 = userStorage.addUser(user1);
        long id2 = userStorage.addUser(user2);
        userStorage.addFriend(id1, id2);
        userStorage.addFriend(id2, id1);
        assertThat(userStorage.removeFriend(id1, id2)).isEqualTo(true);
        assertThat(userStorage.getFriends(id1)).isEmpty();
        assertThat(userStorage.getFriends(id2))
                .isNotEmpty()
                .hasSize(1)
                .contains(user1.withId(id1));
    }

    @Test
    public void removeAbsentFriend() {
        assertThat(userStorage.removeFriend(1, 2)).isEqualTo(false);
    }

}
