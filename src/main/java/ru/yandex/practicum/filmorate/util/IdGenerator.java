package ru.yandex.practicum.filmorate.util;

public class IdGenerator {
    private int id;

    public int getNextId() {
        return ++id;
    }
}
