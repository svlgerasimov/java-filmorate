package ru.yandex.practicum.filmorate.exception;

public class DbCreateEntityFaultException extends RuntimeException {
    public DbCreateEntityFaultException(final String message) {
        super(message);
    }
}
