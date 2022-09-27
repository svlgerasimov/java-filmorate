package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(NotFoundException exception) {
        Map<String, String> result = Map.of("Not Found Error", exception.getMessage());
        log.warn(String.valueOf(result), exception);
        return result;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> result = exception.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError ->
                                String.format("Validation Error in field '%s' with value = '%s'",
                                        fieldError.getField(), fieldError.getRejectedValue()),
                        fieldError -> Objects.requireNonNullElse(fieldError.getDefaultMessage(), "")));
        log.warn(String.valueOf(result), exception);
        return result;
    }

    // HttpMessageNotReadableException выбрасывается например, если отсутствует тело запроса
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        String message = exception.getMessage();
        Map<String, String> result = Map.of("Bad Request", Objects.isNull(message) ? "Details unknown" : message);
        log.warn(String.valueOf(result), exception);
        return result;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleUnexpectedException(Throwable exception) {
        Map<String, String> result = Map.of("Internal Server Error", exception.getMessage());
        log.warn(String.valueOf(result), exception);
        return result;
    }
}
