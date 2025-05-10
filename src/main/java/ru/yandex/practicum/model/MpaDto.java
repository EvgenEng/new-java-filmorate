package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MpaDto {
    private int id; // ID MPA
    private String name; // Название MPA

    public String getName() {
        return this.name.replace("_", "-");
    }
}
