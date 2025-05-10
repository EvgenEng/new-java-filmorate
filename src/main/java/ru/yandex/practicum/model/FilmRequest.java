package ru.yandex.practicum.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.validators.ValidReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmRequest {
    @NotBlank(message = "Film name cannot be blank")
    private String name;

    @Size(max = 200, message = "Description must be less than 200 characters")
    private String description;

    @NotNull(message = "Release date cannot be null")
    @ValidReleaseDate(message = "Release date must be after 1895-12-28")
    private LocalDate releaseDate;

    @Positive(message = "Duration must be positive")
    private int duration;

    @NotNull(message = "MPA rating cannot be null")
    private MpaDto mpa;

    private Set<GenreDto> genres = new HashSet<>();

    // Дополнительная проверка для даты релиза
    @AssertTrue(message = "Release date must be after 1895-12-28")
    private boolean isReleaseDateValid() {
        return releaseDate == null || !releaseDate.isBefore(LocalDate.of(1895, 12, 28));
    }
}
