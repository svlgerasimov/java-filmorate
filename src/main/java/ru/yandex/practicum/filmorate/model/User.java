package ru.yandex.practicum.filmorate.model;

import lombok.Value;
import lombok.With;
import lombok.experimental.NonFinal;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Value
public class User {

    @With
    long id;                       // идентификатор

    @NotBlank(message = "User email is blank")
    @Email(message = "Invalid user email format")
    String email;           // электронная почта

    @NotBlank(message = "User login is blank")
    @Pattern(regexp = "[^ ]+", message = "User login contains a whitespace")
    String login;           // логин пользователя

    @NonFinal
    @With
    String name;                  // имя для отображения

    @NotNull
    @PastOrPresent(message = "User birthday is in future")
    LocalDate birthday;     // дата рождения
}











