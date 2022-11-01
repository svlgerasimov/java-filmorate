package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTest {
    private final MpaDbStorage mpaStorage;

    @Test
    public void getMpaByCorrectIdTest() {
        Optional<Mpa> mpaOptional = mpaStorage.getMpaById(1);
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).isEqualTo(new Mpa(1, "G")));
    }

    @Test
    public void getMpaByIncorrectIdTest() {
        Optional<Mpa> mpaOptional = mpaStorage.getMpaById(-1);
        assertThat(mpaOptional).isEmpty();
    }

    @Test
    public void getAllMpaTest() {
        Collection<Mpa> mpas = mpaStorage.getAllMpa();
        assertThat(mpas)
                .isNotEmpty()
                .contains(new Mpa(1, "G"));
    }
}
