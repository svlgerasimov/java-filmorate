package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class FilmGenreDbStorageTest {
    private final FilmGenreDbStorage filmGenreStorage;
    private final FilmDbStorage filmStorage;

    private final Genre genre1 = new Genre(1, "Комедия");
    private final Genre genre2 = new Genre(2, "Драма");
    private final Genre genre3 = new Genre(3, "Мультфильм");

    @Test
    public void addFilmGenresAndThenGetById() {
        long filmId = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        filmGenreStorage.addFilmGenres(filmId, List.of(genre1, genre2));

        assertThat(filmGenreStorage.getGenresByFilmId(filmId))
                .isNotEmpty()
                .containsOnly(genre1, genre2);
    }

    @Test
    public void addFilmGenresTwice() {
        long filmId = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        filmGenreStorage.addFilmGenres(filmId, List.of(genre1, genre2));
        filmGenreStorage.addFilmGenres(filmId, List.of(genre1));

        assertThat(filmGenreStorage.getGenresByFilmId(filmId))
                .isNotEmpty()
                .containsOnly(genre1, genre2);
    }

    @Test
    public void getAllFilmGenres() {
        long filmId1 = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        long filmId2 = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        filmGenreStorage.addFilmGenres(filmId1, List.of(genre1, genre2));
        filmGenreStorage.addFilmGenres(filmId2, List.of(genre2, genre3));

        assertThat(filmGenreStorage.getAllFilmGenres())
                .isNotEmpty()
                .hasSize(2)
                .containsOnly(Map.entry(filmId1, List.of(genre1, genre2)),
                        Map.entry(filmId2, List.of(genre2, genre3)));
    }

    @Test
    public void getGenresByFilmIds() {
        long filmId1 = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        long filmId2 = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        long filmId3 = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        filmGenreStorage.addFilmGenres(filmId1, List.of(genre1, genre2));
        filmGenreStorage.addFilmGenres(filmId2, List.of(genre2, genre3));
        filmGenreStorage.addFilmGenres(filmId3, List.of(genre3, genre1));

        assertThat(filmGenreStorage.getGenresByFilmIds(List.of(filmId1, filmId2)))
                .isNotEmpty()
                .hasSize(2)
                .containsOnly(Map.entry(filmId1, List.of(genre1, genre2)),
                        Map.entry(filmId2, List.of(genre2, genre3)));
    }

    @Test
    public void getGenresOfFilmWithoutGenres() {
        long filmId = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        assertThat(filmGenreStorage.getGenresByFilmId(filmId))
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void addFilmGenreWithIdOnlyAndThenGetWithAllGenreFields() {
        long filmId1 = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        filmGenreStorage.addFilmGenres(filmId1, List.of(new Genre(genre1.getId(), null)));
        assertThat(filmGenreStorage.getGenresByFilmId(filmId1))
                .isNotEmpty()
                .containsOnly(genre1);

        long filmId2 = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        filmGenreStorage.addFilmGenres(filmId2, List.of(new Genre(genre2.getId(), "")));
        assertThat(filmGenreStorage.getGenresByFilmId(filmId2))
                .isNotEmpty()
                .containsOnly(genre2);

        assertThat(filmGenreStorage.getAllFilmGenres())
                .containsEntry(filmId1, List.of(genre1))
                .containsEntry(filmId2, List.of(genre2));

        assertThat(filmGenreStorage.getGenresByFilmIds(List.of(filmId1, filmId2)))
                .containsEntry(filmId1, List.of(genre1))
                .containsEntry(filmId2, List.of(genre2));
    }

    @Test
    public void addGenresToAbsentFilmAndThenThrowException() {
        assertThatThrownBy(() -> filmGenreStorage.addFilmGenres(-1, List.of(genre1)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void addAbsentGenreToFilmAndThenThrowException() {
        long filmId = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        assertThatThrownBy(() -> filmGenreStorage.addFilmGenres(filmId, List.of(new Genre(-1, ""))))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void deleteFromFilmGenres() {
        long filmId1 = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        long filmId2 = filmStorage.add(TestFilmBuilder.defaultBuilder().build());
        filmGenreStorage.addFilmGenres(filmId1, List.of(genre1));
        filmGenreStorage.addFilmGenres(filmId2, List.of(genre1));
        filmGenreStorage.deleteFilmGenres(filmId1);

        assertThat(filmGenreStorage.getGenresByFilmId(filmId1))
                .isEmpty();
        assertThat(filmGenreStorage.getGenresByFilmId(filmId2))
                .isNotEmpty()
                .containsOnly(genre1);
    }
}
