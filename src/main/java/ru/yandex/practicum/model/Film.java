package ru.yandex.practicum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import jakarta.validation.constraints.*;
import ru.yandex.practicum.validators.ValidReleaseDate;

import java.time.LocalDate;
import java.util.*;

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

    @JsonProperty("mpa")
    private Integer mpaId;

    @JsonIgnore
    private Mpa mpa;

    @JsonProperty("genres")
    private Set<Integer> genreIds = new HashSet<>();

    private Set<Long> likes = new HashSet<>();

    public void setLikesCount(int likesCount) {
    }
}
