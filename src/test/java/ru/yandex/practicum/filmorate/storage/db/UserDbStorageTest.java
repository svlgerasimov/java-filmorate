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

    private final User defaultUser = TestUserBuilder.defaultBuilder().build();
    private final User userPrototype1 = TestUserBuilder.defaultBuilder()
            .email("email1")
            .login("login1")
            .name("name1")
            .birthday(LocalDate.of(2000, Month.JANUARY, 1))
            .build();
    private final User userPrototype2 = TestUserBuilder.defaultBuilder()
            .email("email2")
            .login("login2")
            .name("name2")
            .birthday(LocalDate.of(2000, Month.JANUARY, 2))
            .build();
    private final User userPrototype3 = TestUserBuilder.defaultBuilder()
            .email("email3")
            .login("login3")
            .name("name3")
            .birthday(LocalDate.of(2000, Month.JANUARY, 3))
            .build();

    @Test
    public void getAllUsersWithNoUsersPresent() {
        Collection<User> users = userStorage.getAllUsers();
        assertThat(users).isEmpty();
    }

    @Test
    public void getUserByIncorrectId() {
        long id = userStorage.addUser(defaultUser);
        Optional<User> userOptional = userStorage.getById(id + 1);
        assertThat(userOptional).isEmpty();
    }

    @Test
    public void addAndGetValidUser() {
        User user = defaultUser;
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
        User user = TestUserBuilder.defaultBuilder().email(null).build();
        assertThatThrownBy(() -> userStorage.addUser(user))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addUserWithNullLoginAndThenThrowException() {
        User user = TestUserBuilder.defaultBuilder().login(null).build();
        assertThatThrownBy(() -> userStorage.addUser(user))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addUserWithNullNameAndThenThrowException() {
        User user = TestUserBuilder.defaultBuilder().name(null).build();
        assertThatThrownBy(() -> userStorage.addUser(user))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
    
    @Test
    public void updateUserWithCorrectId() {
        long id1 = userStorage.addUser(userPrototype1);
        long id2 = userStorage.addUser(userPrototype2);
        User user2 = userPrototype2.withId(id2);
        User user3 = userPrototype3.withId(id1);
        assertThat(userStorage.updateUser(user3)).isEqualTo(true);
        assertThat(userStorage.getById(id1))
                .isPresent()
                .hasValue(user3);
        assertThat(userStorage.getAllUsers())
                .isNotEmpty()
                .hasSize(2)
                .containsAll(List.of(user3, user2));
    }

    @Test
    public void updateUserWithAbsentId() {
        long id = userStorage.addUser(userPrototype1);
        User user = userPrototype1.withId(id);
        User updatedUser = userPrototype2.withId(id + 1);
        assertThat(userStorage.updateUser(updatedUser)).isEqualTo(false);
        assertThat(userStorage.getAllUsers())
                .hasSize(1)
                .contains(user);
    }

    @Test
    public void addFriend() {
        User user1 = userPrototype1.withId(userStorage.addUser(userPrototype1));
        User user2 = userPrototype2.withId(userStorage.addUser(userPrototype2));
        User user3 = userPrototype3.withId(userStorage.addUser(userPrototype3));

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
        long id = userStorage.addUser(defaultUser);

        assertThatThrownBy(() -> userStorage.addFriend(id, id + 1))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> userStorage.addFriend(id + 1, id))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addFriendTwice() {
        long id1 = userStorage.addUser(userPrototype1);
        long id2 = userStorage.addUser(userPrototype2);

        userStorage.addFriend(id1, id2);
        assertThatNoException().isThrownBy(() ->
                assertThat(userStorage.addFriend(id1, id2)).isEqualTo(true));
        assertThat(userStorage.getFriends(id1)).hasSize(1);
        assertThat(userStorage.getFriends(id2)).isEmpty();
    }

    @Test
    public void addFriendWithSameIdAndThenThrowException() {
        long id = userStorage.addUser(defaultUser);
        assertThatThrownBy(() -> userStorage.addFriend(id, id))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    public void getCommonFriends() {
        User user1 = userPrototype1.withId(userStorage.addUser(userPrototype1));
        User user2 = userPrototype2.withId(userStorage.addUser(userPrototype2));
        User user3 = userPrototype3.withId(userStorage.addUser(userPrototype3));

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
        User user1 = userPrototype1;
        long id1 = userStorage.addUser(user1);
        long id2 = userStorage.addUser(userPrototype2);
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
