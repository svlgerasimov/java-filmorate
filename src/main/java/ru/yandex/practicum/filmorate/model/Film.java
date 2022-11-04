package ru.yandex.practicum.filmorate.model;

import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;

@Value
public class Film {

    private static final int MAX_FILM_DESCRIPTION_LENGTH = 200;
    private final static String MIN_FILM_RELEASE_DATE = "1895-12-28";

    @With
    long id;                   // идентификатор

    @NotBlank(message = "Film name is blank")
    String name;            // название

    @Size(max = MAX_FILM_DESCRIPTION_LENGTH,
            message = "Film description length is grater then " + MAX_FILM_DESCRIPTION_LENGTH)
    String description;     // описание

    @DateConstraint(minDate = MIN_FILM_RELEASE_DATE,
            message = "Film release date is earlier than " + MIN_FILM_RELEASE_DATE)
    LocalDate releaseDate;  // дата релиза

    @Positive(message = "Film duration is not positive")
    int duration;      // продолжительность

    @NotNull
    Mpa mpa;

    @With
    Collection<Genre> genres;

    int rate;

    //Director director;
}
