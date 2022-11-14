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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class UserDbStorageTest {
    private final UserDbStorage userStorage;

    private final TestUserBuilder userBuilder1 =
            TestUserBuilder.of(0, "email1", "login1", "name1",
                    LocalDate.of(2000, Month.JANUARY, 1));
    private final TestUserBuilder userBuilder2 =
            TestUserBuilder.of(0, "email2", "login2", "name2",
                    LocalDate.of(2000, Month.JANUARY, 2));
    private final TestUserBuilder userBuilder3 =
            TestUserBuilder.of(0, "email3", "login3", "name3",
                    LocalDate.of(2000, Month.JANUARY, 3));

    @Test
    public void getAllUsersWithNoUsersPresent() {
        Collection<User> users = userStorage.getUsers();
        assertThat(users).isEmpty();
    }

    @Test
    public void getUserByIncorrectId() {
        long id = userStorage.add(TestUserBuilder.defaultBuilder().build());
        Optional<User> userOptional = userStorage.getById(id + 1);
        assertThat(userOptional).isEmpty();
    }

    @Test
    public void addAndGetValidUser() {
        User user = TestUserBuilder.defaultBuilder().build();
        long id = userStorage.add(user);

        User userExpected = user.withId(id);
        Optional<User> userOptional = userStorage.getById(id);
        assertThat(userOptional)
                .isPresent()
                .hasValue(userExpected);

        Collection<User> users = userStorage.getUsers();
        assertThat(users)
                .isNotEmpty()
                .hasSize(1)
                .contains(userExpected);
    }

    @Test
    public void addUserWithNullEmailAndThenThrowException() {
        User user = TestUserBuilder.defaultBuilder().withEmail(null).build();
        assertThatThrownBy(() -> userStorage.add(user))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addUserWithNullLoginAndThenThrowException() {
        User user = TestUserBuilder.defaultBuilder().withLogin(null).build();
        assertThatThrownBy(() -> userStorage.add(user))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addUserWithNullNameAndThenThrowException() {
        User user = TestUserBuilder.defaultBuilder().withName(null).build();
        assertThatThrownBy(() -> userStorage.add(user))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void updateUserWithCorrectId() {
        User user1 = userBuilder1.build();
        long id1 = userStorage.add(user1);
        User user2 = userBuilder2.build();
        user2 = user2.withId(userStorage.add(user2));
        User user3 = userBuilder3.withId(id1).build();
        assertThat(userStorage.update(user3)).isEqualTo(true);
        assertThat(userStorage.getById(id1))
                .isPresent()
                .hasValue(user3);
        assertThat(userStorage.getUsers())
                .isNotEmpty()
                .hasSize(2)
                .containsAll(List.of(user3, user2));
    }

    @Test
    public void updateUserWithAbsentId() {
        User user = userBuilder1.build();
        long id = userStorage.add(user);
        user = user.withId(id);
        User updatedUser = userBuilder2.withId(id + 1).build();
        assertThat(userStorage.update(updatedUser)).isEqualTo(false);
        assertThat(userStorage.getUsers())
                .hasSize(1)
                .contains(user);
    }
}
