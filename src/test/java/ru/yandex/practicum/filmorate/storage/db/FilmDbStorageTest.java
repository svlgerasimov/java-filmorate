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
import java.util.Collection;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    private final Film defaultFilm = TestFilmBuilder.defaultBuilder().build();
    private final Film filmPrototype1 = TestFilmBuilder.defaultBuilder()
            .name("name1")
            .description("description1")
            .releaseDate(LocalDate.of(2000, Month.JANUARY, 1))
            .duration(110)
            .mpa(new Mpa(1, "G"))
            .build();
    private final Film filmPrototype2 = TestFilmBuilder.defaultBuilder()
            .name("name2")
            .description("description2")
            .releaseDate(LocalDate.of(2000, Month.JANUARY, 2))
            .duration(120)
            .mpa(new Mpa(2, "PG"))
            .build();
    private final Film filmPrototype3 = TestFilmBuilder.defaultBuilder()
            .name("name3")
            .description("description3")
            .releaseDate(LocalDate.of(2000, Month.JANUARY, 3))
            .duration(130)
            .mpa(new Mpa(3, "PG-13"))
            .build();

    @Test
    public void getAllFilmsWithNoFilmPresent() {
        assertThat(filmStorage.getAllFilms()).isEmpty();
    }

    @Test
    public void getFilmByIncorrectId() {
        long id = filmStorage.addFilm(defaultFilm);
        assertThat(filmStorage.getById(id + 1)).isEmpty();
    }

    @Test
    public void addAndGetValidFilm() {
        long id = filmStorage.addFilm(defaultFilm);
        Film expectedFilm = defaultFilm.withId(id);

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
                TestFilmBuilder.defaultBuilder().mpa(new Mpa(1, null)).build());
        Film expectedFilm = TestFilmBuilder.defaultBuilder().id(id).mpa(new Mpa(1, "G")).build();
        assertThat(filmStorage.getById(id))
                .isPresent()
                .hasValue(expectedFilm);
        assertThat(filmStorage.getAllFilms())
                .isNotEmpty()
                .containsOnly(expectedFilm);
    }

    @Test
    public void addFilmWithNullNameAndThenThrowException() {
        Film film = TestFilmBuilder.defaultBuilder().name(null).build();
        assertThatThrownBy(() -> filmStorage.addFilm(film))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addFilmWithNonPositiveDurationAndThenThrowException() {
        TestFilmBuilder builder = TestFilmBuilder.defaultBuilder().duration(0);
        assertThatThrownBy(() -> filmStorage.addFilm(builder.build()))
                .isInstanceOf(DataIntegrityViolationException.class);
        builder.duration(-1);
        assertThatThrownBy(() -> filmStorage.addFilm(builder.build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void updateFilmWithCorrectId() {
        long id1 = filmStorage.addFilm(filmPrototype1);
        Film film2 = filmPrototype2.withId(filmStorage.addFilm(filmPrototype2));
        Film film3 = filmPrototype3.withId(id1);
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
        Film film1 = filmPrototype1.withId(filmStorage.addFilm(filmPrototype1));
        Film film2 = filmPrototype2.withId(film1.getId() + 1);
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
        long filmId = filmStorage.addFilm(defaultFilm);
        assertThatThrownBy(() -> filmStorage.addLike(filmId, -1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addLikeTwice() {
        long userId = userStorage.addUser(TestUserBuilder.defaultBuilder().build());
        long filmId = filmStorage.addFilm(defaultFilm);
        filmStorage.addLike(filmId, userId);
        assertThatNoException().isThrownBy(() ->
                assertThat(filmStorage.addLike(filmId, userId)).isEqualTo(true));
        assertThat(filmStorage.getById(filmId))
                .hasValueSatisfying(film -> assertThat(film.getRate()).isEqualTo(1));
    }

//    @Test
//    public void getMostPopularFilms() {
//        User user = TestUserBuilder.defaultBuilder().build();
//        long userId1 = userStorage.addUser(user);
//        long userId2 = userStorage.addUser(user);
//        long userId3 = userStorage.addUser(user);
//        long filmId1 = filmStorage.addFilm(filmPrototype1);
//        long filmId2 = filmStorage.addFilm(filmPrototype2);
//        long filmId3 = filmStorage.addFilm(filmPrototype3);
//        filmStorage.addLike(filmId1, userId1);
//        filmStorage.addLike(filmId1, userId2);
//        filmStorage.addLike(filmId2, userId1);
//        filmStorage.addLike(filmId2, userId2);
//        filmStorage.addLike(filmId2, userId3);
//        filmStorage.addLike(filmId3, userId1);
//
//        Collection<Film> mostPopular1 = filmStorage.getMostPopularFilms(1);
//        assertThat(mostPopular1)
//                .hasSize(1);
//        assertThat(mostPopular1.)
//
//        Collection<Film> mostPopular3 = filmStorage.getMostPopularFilms(3);
//        Collection<Film> mostPopular4 = filmStorage.getMostPopularFilms(4);
//
//        assertThat(filmStorage.getMostPopularFilms(4)).containsExactly(film2, film1, film3);
//    }
}
