package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {

    private int id;                       // идентификатор

    @NotBlank(message = "User email is blank")
    @Email(message = "Invalid user email format")
    private final String email;           // электронная почта

    @NotBlank(message = "User login is blank")
    @Pattern(regexp = "[^ ]+", message = "User login contains a whitespace")
    private final String login;           // логин пользователя

    private String name;                  // имя для отображения

    @PastOrPresent(message = "User birthday is in future")
    private final LocalDate birthday;     // дата рождения
}
