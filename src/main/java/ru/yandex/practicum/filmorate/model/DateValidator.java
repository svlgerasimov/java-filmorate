package ru.yandex.practicum.filmorate.model;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DateValidator implements
        ConstraintValidator<DateConstraint, LocalDate> {

    private LocalDate minDate;
    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;


    @Override
    public void initialize(DateConstraint constraint) {
        minDate = LocalDate.parse(constraint.minDate(), DATE_FORMATTER);
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext cxt) {
        // Дата может быть не указана. Но если она есть, должна быть не раньше минимальной даты.
        return Objects.isNull(releaseDate) || !releaseDate.isBefore(minDate);
    }

}