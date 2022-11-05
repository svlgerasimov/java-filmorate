package ru.yandex.practicum.filmorate.model;

import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotNull;

@Value
public class Review {

    @With
    long id;

    @NotNull
    String content;
    boolean isPositive; // Тип отзыва

    @NotNull
    long userId;  // Пользователь

    @NotNull
    long filmId;  // Фильм
    int useful;   // рейтинг полезности
}
