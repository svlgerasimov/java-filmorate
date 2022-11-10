package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;


@Value
public class Director {

    @With
     long id;

    @NotBlank(message = "Director name is blank")
     String name;
}
