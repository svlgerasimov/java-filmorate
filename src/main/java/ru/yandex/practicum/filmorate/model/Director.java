package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@ToString
@Getter
@Setter
@AllArgsConstructor
public class Director {

    long id;

    @NotBlank(message = "Director name is blank")
    String name;
}
