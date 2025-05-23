package ru.yandex.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenreDto {
    private int id; // ID жанра
    private String name; // Название жанра
}
