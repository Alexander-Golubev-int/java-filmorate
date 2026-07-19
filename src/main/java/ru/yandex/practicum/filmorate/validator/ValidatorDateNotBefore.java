package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.DateNotBefore;

import java.time.LocalDate;

public class ValidatorDateNotBefore implements ConstraintValidator<DateNotBefore, LocalDate> {

    private LocalDate localDate;

    @Override
    public void initialize(DateNotBefore annotation) {
        this.localDate = LocalDate.parse(annotation.value());
    }

    @Override
    public boolean isValid(LocalDate reqestLocalDate, ConstraintValidatorContext constraintValidatorContext) {
        if (reqestLocalDate == null) {
            return true;
        }
        return !reqestLocalDate.isBefore(localDate);
    }
}
