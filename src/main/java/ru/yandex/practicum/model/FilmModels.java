package ru.yandex.practicum.model;

import jakarta.validation.constraints.*;
import lombok.Data;
//import ru.yandex.practicum.validators.ValidReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FilmModels {
    @Data
    public static class FilmRequest {
        @NotBlank
        private String name;

        @Size(max = 200)
        private String description;

        @NotNull
        //@PastOrPresent(message = "Release date cannot be in the future")
        //@ValidReleaseDate
        private LocalDate releaseDate;

        @Positive
        private int duration;

        @NotNull
        private MpaDto mpa;

        private Set<GenreDto> genres = new HashSet<>();

        @AssertTrue(message = "Release date must be after 1895-12-28")
        public boolean isReleaseDateValid() {
            return releaseDate.isAfter(LocalDate.of(1895, 12, 28));
        }
    }
}