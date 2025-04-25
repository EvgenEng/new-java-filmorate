package ru.yandex.practicum.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FilmGenre {
    COMEDY("Комедия"),
    DRAMA("Драма"),
    ANIMATION("Мультфильм"),
    THRILLER("Триллер"),
    DOCUMENTARY("Документальный"),
    ACTION("Боевик");

    private final String name;

    FilmGenre(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
