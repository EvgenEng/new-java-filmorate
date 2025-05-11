/*package ru.yandex.practicum.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.yandex.practicum.controllers.FilmController;

import java.time.LocalDate;

@Component
public class FilmRequestValidator implements Validator {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean supports(Class<?> clazz) {
        return FilmController.FilmRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FilmController.FilmRequest filmRequest = (FilmController.FilmRequest) target;

        if (filmRequest.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            errors.rejectValue("releaseDate",
                    "releaseDate.invalid",
                    "Дата релиза не может быть раньше " + MIN_RELEASE_DATE);
        }
    }
}*/
