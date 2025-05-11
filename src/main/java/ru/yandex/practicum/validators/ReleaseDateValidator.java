/*package ru.yandex.practicum.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ValidReleaseDate, LocalDate> {
    private static final LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        boolean isValid = !value.isBefore(EARLIEST_DATE);
        System.out.println("ReleaseDateValidator: " + value + " is valid? " + isValid); // Логирование
        return isValid;
    }
}
*/