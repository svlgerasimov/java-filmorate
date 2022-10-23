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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class FriendsDbStorageTest {
    private final UserDbStorage userStorage;
    private final FriendsDbStorage friendsStorage;

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
    public void addFriend() {
        User user1 = userBuilder1.build();
        User user2 = userBuilder2.build();
        User user3 = userBuilder3.build();
        user1 = user1.withId(userStorage.addUser(user1));
        user2 = user2.withId(userStorage.addUser(user2));
        user3 = user3.withId(userStorage.addUser(user3));

        assertThat(friendsStorage.addFriend(user1.getId(), user2.getId())).isEqualTo(true);
        assertThat(friendsStorage.addFriend(user1.getId(), user3.getId())).isEqualTo(true);
        assertThat(friendsStorage.addFriend(user2.getId(), user1.getId())).isEqualTo(true);
        assertThat(friendsStorage.addFriend(user2.getId(), user3.getId())).isEqualTo(true);

        assertThat(friendsStorage.getFriends(user1.getId()))
                .isNotEmpty()
                .hasSize(2)
                .containsAll(List.of(user2, user3));
        assertThat(friendsStorage.getFriends(user2.getId()))
                .isNotEmpty()
                .hasSize(2)
                .containsAll(List.of(user1, user3));
        assertThat(friendsStorage.getFriends(user3.getId()))
                .isEmpty();
    }

    @Test
    public void addFriendWithAbsentIdAndThenThrowException() {
        long id = userStorage.addUser(TestUserBuilder.defaultBuilder().build());

        assertThatThrownBy(() -> friendsStorage.addFriend(id, id + 1))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> friendsStorage.addFriend(id + 1, id))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addFriendTwice() {
        long id1 = userStorage.addUser(userBuilder1.build());
        long id2 = userStorage.addUser(userBuilder2.build());

        friendsStorage.addFriend(id1, id2);
        assertThatNoException().isThrownBy(() ->
                assertThat(friendsStorage.addFriend(id1, id2)).isEqualTo(true));
        assertThat(friendsStorage.getFriends(id1)).hasSize(1);
        assertThat(friendsStorage.getFriends(id2)).isEmpty();
    }

    @Test
    public void addFriendWithSameIdAndThenThrowException() {
        long id = userStorage.addUser(TestUserBuilder.defaultBuilder().build());
        assertThatThrownBy(() -> friendsStorage.addFriend(id, id))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    public void getCommonFriends() {
        User user1 = userBuilder2.build();
        user1 = user1.withId(userStorage.addUser(user1));
        User user2 = userBuilder2.build();
        user2 = user2.withId(userStorage.addUser(user2));
        User user3 = userBuilder3.build();
        user3= user3.withId(userStorage.addUser(user3));

        friendsStorage.addFriend(user1.getId(), user2.getId());
        friendsStorage.addFriend(user1.getId(), user3.getId());
        friendsStorage.addFriend(user2.getId(), user1.getId());
        friendsStorage.addFriend(user2.getId(), user3.getId());

        assertThat(friendsStorage.getCommonFriends(user1.getId(), user2.getId()))
                .isNotEmpty()
                .hasSize(1)
                .contains(user3);
    }

    @Test
    public void removeFriend() {
        User user1 = userBuilder1.build();
        long id1 = userStorage.addUser(user1);
        long id2 = userStorage.addUser(userBuilder2.build());
        friendsStorage.addFriend(id1, id2);
        friendsStorage.addFriend(id2, id1);
        assertThat(friendsStorage.removeFriend(id1, id2)).isEqualTo(true);
        assertThat(friendsStorage.getFriends(id1)).isEmpty();
        assertThat(friendsStorage.getFriends(id2))
                .isNotEmpty()
                .hasSize(1)
                .contains(user1.withId(id1));
    }

    @Test
    public void removeAbsentFriend() {
        assertThat(friendsStorage.removeFriend(1, 2)).isEqualTo(false);
    }
}
