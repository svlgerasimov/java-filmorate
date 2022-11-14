package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class LikesDbStorageTest {
    private final LikesDbStorage likesStorage;
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Test
    public void addLike() {
        TestUserBuilder userBuilder1 = TestUserBuilder.defaultBuilder()
                .withEmail("email1")
                .withLogin("login1");
        TestUserBuilder userBuilder2 = TestUserBuilder.defaultBuilder()
                .withEmail("email2")
                .withLogin("login2");
        long userId1 = userStorage.add(userBuilder1.build());
        long userId2 = userStorage.add(userBuilder2.build());
        Film defaultFilm = TestFilmBuilder.defaultBuilder().build();
        long filmId1 = filmStorage.add(defaultFilm);
        long filmId2 = filmStorage.add(defaultFilm);
        assertThat(likesStorage.addLike(filmId1, userId1)).isEqualTo(true);
        assertThat(likesStorage.addLike(filmId1, userId2)).isEqualTo(true);
        assertThat(filmStorage.getById(filmId1))
                .hasValueSatisfying(film ->
                        assertThat(film.getRate()).isEqualTo(2));
        assertThat(filmStorage.getById(filmId2))
                .hasValueSatisfying(film ->
                        assertThat(film.getRate()).isEqualTo(0));
    }

    @Test
    public void addLikeWithAbsentFilmAndThenThrowException() {
        long userId = userStorage.add(TestUserBuilder.defaultBuilder().build());
        assertThatThrownBy(() -> likesStorage.addLike(-1, userId))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addLikeWithAbsentUserAndThenThrowException() {
        long filmId = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        assertThatThrownBy(() -> likesStorage.addLike(filmId, -1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addLikeTwice() {
        long userId = userStorage.add(TestUserBuilder.defaultBuilder().build());
        long filmId = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        likesStorage.addLike(filmId, userId);
        assertThatNoException().isThrownBy(() ->
                assertThat(likesStorage.addLike(filmId, userId)).isEqualTo(true));
        assertThat(filmStorage.getById(filmId))
                .hasValueSatisfying(film -> assertThat(film.getRate()).isEqualTo(1));
    }

    @Test
    public void getMostPopularFilms() {
        TestUserBuilder userBuilder1 = TestUserBuilder.defaultBuilder()
                .withEmail("email1")
                .withLogin("login1");
        TestUserBuilder userBuilder2 = TestUserBuilder.defaultBuilder()
                .withEmail("email2")
                .withLogin("login2");
        TestUserBuilder userBuilder3 = TestUserBuilder.defaultBuilder()
                .withEmail("email3")
                .withLogin("login3");
        long userId1 = userStorage.add(userBuilder1.build());
        long userId2 = userStorage.add(userBuilder2.build());
        long userId3 = userStorage.add(userBuilder3.build());
        TestFilmBuilder filmBuilder1 = TestFilmBuilder.defaultBuilder().withName("name1");
        TestFilmBuilder filmBuilder2 = TestFilmBuilder.defaultBuilder().withName("name2");
        TestFilmBuilder filmBuilder3 = TestFilmBuilder.defaultBuilder().withName("name3");
        long filmId1 = filmStorage.add(filmBuilder1.build());
        long filmId2 = filmStorage.add(filmBuilder2.build());
        long filmId3 = filmStorage.add(filmBuilder3.build());
        likesStorage.addLike(filmId1, userId1);
        likesStorage.addLike(filmId1, userId2);
        likesStorage.addLike(filmId2, userId1);
        likesStorage.addLike(filmId2, userId2);
        likesStorage.addLike(filmId2, userId3);
        likesStorage.addLike(filmId3, userId1);

        Film film1 = filmBuilder1.withId(filmId1).withRate(2).build();
        Film film2 = filmBuilder2.withId(filmId2).withRate(3).build();
        Film film3 = filmBuilder3.withId(filmId3).withRate(1).build();

        assertThat(filmStorage.getMostPopularFilms(1, null, null))
                .isNotEmpty()
                .hasSize(1)
                .contains(film2);
        assertThat(filmStorage.getMostPopularFilms(3, null, null))
                .isNotEmpty()
                .hasSize(3)
                .containsExactly(film2, film1, film3);
        assertThat(filmStorage.getMostPopularFilms(4, null, null))
                .isNotEmpty()
                .hasSize(3)
                .containsExactly(film2, film1, film3);
    }

    @Test
    public void removeLike() {
        TestUserBuilder userBuilder1 = TestUserBuilder.defaultBuilder()
                .withEmail("email1")
                .withLogin("login1");
        TestUserBuilder userBuilder2 = TestUserBuilder.defaultBuilder()
                .withEmail("email2")
                .withLogin("login2");
        long userId1 = userStorage.add(userBuilder1.build());
        long userId2 = userStorage.add(userBuilder2.build());
        Film defaultFilm = TestFilmBuilder.defaultBuilder().build();
        long filmId1 = filmStorage.add(defaultFilm);
        filmStorage.add(defaultFilm);
        likesStorage.addLike(filmId1, userId1);
        likesStorage.addLike(filmId1, userId2);
        assertThat(likesStorage.removeLike(filmId1, userId1)).isEqualTo(true);
        assertThat(filmStorage.getById(filmId1))
                .hasValueSatisfying(film ->
                        assertThat(film.getRate()).isEqualTo(1));
    }

    @Test
    public void removeLikeTwice() {
        TestUserBuilder userBuilder1 = TestUserBuilder.defaultBuilder()
                .withEmail("email1")
                .withLogin("login1");
        TestUserBuilder userBuilder2 = TestUserBuilder.defaultBuilder()
                .withEmail("email2")
                .withLogin("login2");
        long userId1 = userStorage.add(userBuilder1.build());
        long userId2 = userStorage.add(userBuilder2.build());
        Film defaultFilm = TestFilmBuilder.defaultBuilder().build();
        long filmId1 = filmStorage.add(defaultFilm);
        filmStorage.add(defaultFilm);
        likesStorage.addLike(filmId1, userId1);
        likesStorage.addLike(filmId1, userId2);
        likesStorage.removeLike(filmId1, userId1);
        assertThat(likesStorage.removeLike(filmId1, userId1)).isEqualTo(false);
        assertThat(filmStorage.getById(filmId1))
                .hasValueSatisfying(film ->
                        assertThat(film.getRate()).isEqualTo(1));
    }

    @Test
    public void removeLikeAbsentFilm() {
        long userId = userStorage.add(TestUserBuilder.defaultBuilder().build());
        long filmId = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        likesStorage.addLike(filmId, userId);
        assertThat(likesStorage.removeLike(filmId + 1, userId)).isEqualTo(false);
        assertThat(filmStorage.getById(filmId))
                .hasValueSatisfying(film ->
                        assertThat(film.getRate()).isEqualTo(1));
    }

    @Test
    public void removeLikeAbsentUser() {
        long userId = userStorage.add(TestUserBuilder.defaultBuilder().build());
        long filmId = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        likesStorage.addLike(filmId, userId);
        assertThat(likesStorage.removeLike(filmId, userId + 1)).isEqualTo(false);
        assertThat(filmStorage.getById(filmId))
                .hasValueSatisfying(film ->
                        assertThat(film.getRate()).isEqualTo(1));
    }
}
