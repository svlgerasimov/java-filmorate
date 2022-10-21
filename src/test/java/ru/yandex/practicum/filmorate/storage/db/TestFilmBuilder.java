package ru.yandex.practicum.filmorate.storage.db;

import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@NoArgsConstructor(staticName = "defaultBuilder")
public class TestFilmBuilder {
    private long id;
    private String name = "name";
    private String description = "description";
    private LocalDate releaseDate = LocalDate.of(2000, Month.JANUARY, 1);
    private int duration = 120;
    private Mpa mpa = new Mpa(1, "G");
    private List<Genre> genres;

    private int rate;

    public TestFilmBuilder id(long id) {
        this.id = id;
        return this;
    }

    public TestFilmBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TestFilmBuilder description(String description) {
        this.description = description;
        return this;
    }

    public TestFilmBuilder releaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public TestFilmBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public TestFilmBuilder mpa(Mpa mpa) {
        this.mpa = mpa;
        return this;
    }

    public TestFilmBuilder genres(List<Genre> genres) {
        this.genres = genres;
        return this;
    }

    public TestFilmBuilder rate(int rate) {
        this.rate = rate;
        return this;
    }

    public Film build() {
        return new Film(id, name, description, releaseDate, duration, mpa, genres, rate);
    }
}
