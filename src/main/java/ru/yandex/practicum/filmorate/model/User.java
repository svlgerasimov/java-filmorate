package ru.yandex.practicum.filmorate.model;

import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Value
public class User {

    @NonFinal @Setter
    int id;                       // идентификатор

    @NotBlank(message = "User email is blank")
    @Email(message = "Invalid user email format")
    String email;           // электронная почта

    @NotBlank(message = "User login is blank")
    @Pattern(regexp = "[^ ]+", message = "User login contains a whitespace")
    String login;           // логин пользователя

    @NonFinal @Setter
    String name;                  // имя для отображения

    @PastOrPresent(message = "User birthday is in future")
    LocalDate birthday;     // дата рождения
}
