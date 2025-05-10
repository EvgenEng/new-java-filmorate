package ru.yandex.practicum.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FilmGenre {
    COMEDY("Комедия"),
    DRAMA("Драма"),
    ANIMATION("Мультфильм"),
    THRILLER("Триллер"),
    DOCUMENTARY("Документальный"),
    ACTION("Боевик");

    private final String russianName;

    FilmGenre(String russianName) {
        this.russianName = russianName;
    }

    @JsonValue
    public String getRussianName() {
        return russianName;
    }

    public int getId() {
        return this.ordinal() + 1;
    }
}
