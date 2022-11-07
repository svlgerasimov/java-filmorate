package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Value
public class Review {

    @With
    long reviewId;

    @NotNull
    String content;

    @NotNull
    Boolean isPositive; // Тип отзыва

    @NotNull
   // @Size(min = 1)
    Long userId;  // Пользователь

    @NotNull
          //  @Size(min = 1)
   Long filmId;  // Фильм

    int useful;   // рейтинг полезности


}
