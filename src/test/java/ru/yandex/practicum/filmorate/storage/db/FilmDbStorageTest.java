package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    private static final Mpa MPA_CORRECT = new Mpa(1, "G");

    @Test
    public void getAllFilmsWithNoFilmPresent() {
        assertThat(filmStorage.getAllFilms()).isEmpty();
    }

    @Test
    public void getFilmByIncorrectId() {
        Film film = new Film(0, "name", "description",
                LocalDate.of(2001, Month.JANUARY, 1), 120,
                new Mpa(1, ""), null);
        long id = filmStorage.addFilm(film);
        assertThat(filmStorage.getById(id + 1)).isEmpty();
    }

    @Test
    public void addAndGetValidFilm() {
        Film film = new Film(0, "name", "description",
                LocalDate.of(2001, Month.JANUARY, 1), 120,
                MPA_CORRECT, null);
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
        Film film = new Film(0, "name", "description",
                LocalDate.of(2001, Month.JANUARY, 1), 120,
                new Mpa(MPA_CORRECT.getId(), null), null);
        long id = filmStorage.addFilm(film);
        Film expectedFilm = new Film(id, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), MPA_CORRECT, null);
        assertThat(filmStorage.getById(id))
                .isPresent()
                .hasValue(expectedFilm);
        assertThat(filmStorage.getAllFilms())
                .isNotEmpty()
                .containsOnly(expectedFilm);
    }

}
