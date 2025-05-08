package ru.yandex.practicum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import jakarta.validation.constraints.*;
import ru.yandex.practicum.validators.ValidReleaseDate;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

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

    // Принимаем просто число (ID) для MPA
    @JsonProperty("mpa")
    private Integer mpaId;

    // Преобразуем ID в MpaRating при необходимости
    @JsonIgnore
    public MpaRating getMpaRating() {
        return mpaId != null ? MpaRating.fromId(mpaId) : null;
    }

    // Принимаем массив чисел (ID) для жанров
    @JsonProperty("genres")
    private Set<Integer> genreIds;

    // Преобразуем ID в FilmGenre при необходимости
    @JsonIgnore
    public Set<FilmGenre> getGenres() {
        if (genreIds == null) return Set.of();
        return genreIds.stream()
                .map(id -> FilmGenre.values()[id - 1])
                .collect(Collectors.toSet());
    }

    private Set<Long> likes;
}
