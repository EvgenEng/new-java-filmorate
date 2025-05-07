package ru.yandex.practicum.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MpaRating {
    G(1, "G", "У фильма нет возрастных ограничений"),
    PG(2, "PG", "Детям рекомендуется смотреть фильм с родителями"),
    PG_13(3, "PG-13", "Детям до 13 лет просмотр не желателен"),
    R(4, "R", "Лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
    NC_17(5, "NC-17", "Лицам до 18 лет просмотр запрещён");

    private final int id;
    private final String code;
    private final String description;

    MpaRating(int id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public static MpaRating fromId(int id) {
        return Arrays.stream(values())
                .filter(rating -> rating.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown MPA rating id: " + id));
    }
}
