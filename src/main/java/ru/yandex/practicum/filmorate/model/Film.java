package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {

    private static final int MAX_FILM_DESCRIPTION_LENGTH = 200;
    private final static String MIN_FILM_RELEASE_DATE = "1895-12-28";

    private int id;                   // идентификатор

    @NotBlank(message = "Film name is blank")
    private final String name;            // название

    @Size(max = MAX_FILM_DESCRIPTION_LENGTH,
            message = "Film description length is grater then " + MAX_FILM_DESCRIPTION_LENGTH)
    private final String description;     // описание

    @DateConstraint(minDate = MIN_FILM_RELEASE_DATE,
            message = "Film release date is earlier than " + MIN_FILM_RELEASE_DATE)
    private final LocalDate releaseDate;  // дата релиза

    @Positive(message = "Film duration is not positive")
    private final int duration;      // продолжительность
}
