package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    public void getGenreByCorrectIdTest() {
        Optional<Genre> genreOptional = genreDbStorage.getGenreById(1);
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "Комедия"));
    }

    @Test
    public void getGenreByIncorrectIdTest() {
        Optional<Genre> genreOptional = genreDbStorage.getGenreById(-1);
        assertThat(genreOptional).isEmpty();
    }

    @Test
    public void getAllGenresTest() {
        Collection<Genre> genres = genreDbStorage.getAllGenres();
        assertThat(genres)
                .isNotEmpty()
                .contains(new Genre(1, "Комедия"));
    }

    @Test
    public void getGenresByIdsTest() {
        Map<Integer, Genre> genres = genreDbStorage.getGenresByIds(List.of(1, 2));
        assertThat(genres)
                .isNotEmpty()
                .contains(Map.entry(1, new Genre(1, "Комедия")))
                .contains(Map.entry(2, new Genre(2, "Драма")))
                .doesNotContainKey(-1);
    }
}