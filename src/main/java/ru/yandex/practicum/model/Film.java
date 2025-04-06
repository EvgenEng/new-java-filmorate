package ru.yandex.practicum.model;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import ru.yandex.practicum.validators.ValidReleaseDate;

@Data
public class Film {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @ValidReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительной")
    private int duration;
}
