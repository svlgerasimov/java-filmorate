package ru.yandex.practicum.filmorate.model;

import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotNull;

@Value
public class Review {

    @With
    long reviewId;

    @NotNull
    String content;

    @NotNull
    Boolean isPositive; // Тип отзыва

    @NotNull
    Long userId;  // Пользователь

    @NotNull
    Long filmId;  // Фильм

    int useful;   // рейтинг полезности


}
