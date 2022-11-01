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
    public void addFilmWithNullMpaAndThenThrowException() {
        Film film = TestFilmBuilder.defaultBuilder().withMpa(null).build();
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
}
