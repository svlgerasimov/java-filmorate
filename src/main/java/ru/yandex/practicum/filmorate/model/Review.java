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
@JsonIgnoreProperties({"positive"})
public class Review {

    @With
    long reviewId;

    @NotNull
    String content;

    @NotNull
    @JsonProperty(value="isPositive")
    Boolean isPositive; // Тип отзыва

    @NotNull
    @Min(-5)
    long userId;  // Пользователь

    @NotNull
    @Min(-5)
    long filmId;  // Фильм

    int useful;   // рейтинг полезности


}
