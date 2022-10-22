package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    private final TestFilmBuilder filmBuilder1 =
        TestFilmBuilder.of(0, "name1" , "description1",
                LocalDate.of(2000, Month.JANUARY, 1), 110, new Mpa(1, "G"),
                null, 0);
    private final TestFilmBuilder filmBuilder2 =
            TestFilmBuilder.of(0, "name2" , "description2",
                    LocalDate.of(2000, Month.JANUARY, 2), 120, new Mpa(2, "PG"),
                    null, 0);
    private final TestFilmBuilder filmBuilder3 =
            TestFilmBuilder.of(0, "name3" , "description3",
                    LocalDate.of(2000, Month.JANUARY, 3), 130, new Mpa(3, "PG-13"),
                    null, 0);

    @Test
    public void getAllFilmsWithNoFilmPresent() {
        assertThat(filmStorage.getAllFilms()).isEmpty();
    }

    @Test
    public void getFilmByIncorrectId() {
        long id = filmStorage.addFilm(TestFilmBuilder.defaultBuilder().build());
        assertThat(filmStorage.getById(id + 1)).isEmpty();
    }

    @Test
    public void addAndGetValidFilm() {
        Film film = TestFilmBuilder.defaultBuilder().build();
        long id = filmStorage.addFilm(film);
        Film expectedFilm = film.withId(id);

        assertThat(filmStorage.getById(id))
                .isPresent()
                .hasValue(expectedFilm);
        assertThat(filmStorage.getAllFilms())
                .isNotEmpty()
                .containsOnly(expectedFilm);
    }

    @Test
    public void addFilmWithMpaIdAndThenGetWithAllMpaFields() {
        long id = filmStorage.addFilm(
                TestFilmBuilder.defaultBuilder().withMpa(new Mpa(1, null)).build());
        Film expectedFilm = TestFilmBuilder.defaultBuilder().withId(id).withMpa(new Mpa(1, "G")).build();
        assertThat(filmStorage.getById(id))
                .isPresent()
                .hasValue(expectedFilm);
        assertThat(filmStorage.getAllFilms())
                .isNotEmpty()
                .containsOnly(expectedFilm);
    }

    @Test
    public void addFilmWithNullNameAndThenThrowException() {
        Film film = TestFilmBuilder.defaultBuilder().withName(null).build();
        assertThatThrownBy(() -> filmStorage.addFilm(film))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addFilmWithNonPositiveDurationAndThenThrowException() {
        TestFilmBuilder builder1 = TestFilmBuilder.defaultBuilder().withDuration(0);
        assertThatThrownBy(() -> filmStorage.addFilm(builder1.build()))
                .isInstanceOf(DataIntegrityViolationException.class);
        TestFilmBuilder builder2 = TestFilmBuilder.defaultBuilder().withDuration(-1);
        assertThatThrownBy(() -> filmStorage.addFilm(builder2.build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void updateFilmWithCorrectId() {
        long id1 = filmStorage.addFilm(filmBuilder1.build());
        Film film2 = filmBuilder2.build();
        film2 = film2.withId(filmStorage.addFilm(film2));
        Film film3 = filmBuilder3.build().withId(id1);
        assertThat(filmStorage.updateFilm(film3)).isEqualTo(true);
        assertThat(filmStorage.getById(id1))
                .isPresent()
                .hasValue(film3);
        assertThat(filmStorage.getAllFilms())
                .hasSize(2)
                .isNotEmpty()
                .contains(film2, film3);
    }

    @Test
    public void updateFilmWithAbsentId() {
        Film film1 = filmBuilder1.build();
        film1 = film1.withId(filmStorage.addFilm(film1));
        Film film2 = filmBuilder2.build();
        film2 = film2.withId(film1.getId() + 1);
        assertThat(filmStorage.updateFilm(film2)).isEqualTo(false);
        assertThat(filmStorage.getAllFilms())
                .hasSize(1)
                .contains(film1);
    }

    @Test
    public void addLike() {
        User user = TestUserBuilder.defaultBuilder().build();
        long userId1 = userStorage.addUser(user);
        long userId2 = userStorage.addUser(user);
        Film defaultFilm = TestFilmBuilder.defaultBuilder().build();
        long filmId1 = filmStorage.addFilm(defaultFilm);
        long filmId2 = filmStorage.addFilm(defaultFilm);
        assertThat(filmStorage.addLike(filmId1, userId1)).isEqualTo(true);
        assertThat(filmStorage.addLike(filmId1, userId2)).isEqualTo(true);
        assertThat(filmStorage.getById(filmId1))
                .hasValueSatisfying(film ->
                        assertThat(film.getRate()).isEqualTo(2));
        assertThat(filmStorage.getById(filmId2))
                .hasValueSatisfying(film ->
                        assertThat(film.getRate()).isEqualTo(0));
    }

    @Test
    public void addLikeWithAbsentFilmAndThenThrowException() {
        long userId = userStorage.addUser(TestUserBuilder.defaultBuilder().build());
        assertThatThrownBy(() -> filmStorage.addLike(-1, userId))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addLikeWithAbsentUserAndThenThrowException() {
        long filmId = filmStorage.addFilm(TestFilmBuilder.defaultBuilder().build());
        assertThatThrownBy(() -> filmStorage.addLike(filmId, -1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addLikeTwice() {
        long userId = userStorage.addUser(TestUserBuilder.defaultBuilder().build());
        long filmId = filmStorage.addFilm(TestFilmBuilder.defaultBuilder().build());
        filmStorage.addLike(filmId, userId);
        assertThatNoException().isThrownBy(() ->
                assertThat(filmStorage.addLike(filmId, userId)).isEqualTo(true));
        assertThat(filmStorage.getById(filmId))
                .hasValueSatisfying(film -> assertThat(film.getRate()).isEqualTo(1));
    }

    @Test
    public void getMostPopularFilms() {
        User user = TestUserBuilder.defaultBuilder().build();
        long userId1 = userStorage.addUser(user);
        long userId2 = userStorage.addUser(user);
        long userId3 = userStorage.addUser(user);
        long filmId1 = filmStorage.addFilm(filmBuilder1.build());
        long filmId2 = filmStorage.addFilm(filmBuilder2.build());
        long filmId3 = filmStorage.addFilm(filmBuilder3.build());
        filmStorage.addLike(filmId1, userId1);
        filmStorage.addLike(filmId1, userId2);
        filmStorage.addLike(filmId2, userId1);
        filmStorage.addLike(filmId2, userId2);
        filmStorage.addLike(filmId2, userId3);
        filmStorage.addLike(filmId3, userId1);

        Film film1 = filmBuilder1.withId(filmId1).withRate(2).build();
        Film film2 = filmBuilder2.withId(filmId2).withRate(3).build();
        Film film3 = filmBuilder3.withId(filmId3).withRate(1).build();

        assertThat(filmStorage.getMostPopularFilms(1))
                .isNotEmpty()
                .hasSize(1)
                .contains(film2);
        assertThat(filmStorage.getMostPopularFilms(3))
                .isNotEmpty()
                .hasSize(3)
                .containsExactly(film2, film1, film3);
        assertThat(filmStorage.getMostPopularFilms(4))
                .isNotEmpty()
                .hasSize(3)
                .containsExactly(film2, film1, film3);
    }

    @Test
    public void removeLike() {
        User user = TestUserBuilder.defaultBuilder().build();
        long userId1 = userStorage.addUser(user);
        long userId2 = userStorage.addUser(user);
        Film defaultFilm = TestFilmBuilder.defaultBuilder().build();
        long filmId1 = filmStorage.addFilm(defaultFilm);
        filmStorage.addFilm(defaultFilm);
        filmStorage.addLike(filmId1, userId1);
        filmStorage.addLike(filmId1, userId2);
        assertThat(filmStorage.removeLike(filmId1, userId1)).isEqualTo(true);
        assertThat(filmStorage.getById(filmId1))
                .hasValueSatisfying(film ->
                        assertThat(film.getRate()).isEqualTo(1));
    }

    @Test
    public void removeLikeTwice() {
        User user = TestUserBuilder.defaultBuilder().build();
        long userId1 = userStorage.addUser(user);
        long userId2 = userStorage.addUser(user);
        Film defaultFilm = TestFilmBuilder.defaultBuilder().build();
        long filmId1 = filmStorage.addFilm(defaultFilm);
        filmStorage.addFilm(defaultFilm);
        filmStorage.addLike(filmId1, userId1);
        filmStorage.addLike(filmId1, userId2);
        filmStorage.removeLike(filmId1, userId1);
        assertThat(filmStorage.removeLike(filmId1, userId1)).isEqualTo(false);
        assertThat(filmStorage.getById(filmId1))
                .hasValueSatisfying(film ->
                        assertThat(film.getRate()).isEqualTo(1));
    }

    @Test
    public void removeLikeAbsentFilm() {
        long userId = userStorage.addUser(TestUserBuilder.defaultBuilder().build());
        long filmId = filmStorage.addFilm(TestFilmBuilder.defaultBuilder().build());
        filmStorage.addLike(filmId, userId);
        assertThat(filmStorage.removeLike(filmId + 1, userId)).isEqualTo(false);
        assertThat(filmStorage.getById(filmId))
                .hasValueSatisfying(film ->
                        assertThat(film.getRate()).isEqualTo(1));
    }

    @Test
    public void removeLikeAbsentUser() {
        long userId = userStorage.addUser(TestUserBuilder.defaultBuilder().build());
        long filmId = filmStorage.addFilm(TestFilmBuilder.defaultBuilder().build());
        filmStorage.addLike(filmId, userId);
        assertThat(filmStorage.removeLike(filmId, userId + 1)).isEqualTo(false);
        assertThat(filmStorage.getById(filmId))
                .hasValueSatisfying(film ->
                        assertThat(film.getRate()).isEqualTo(1));
    }
}
