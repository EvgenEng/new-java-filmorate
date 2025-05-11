package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MpaDto {
    private int id;
    private String name;

    public String getName() {
        return this.name.replace("_", "-");
    }
}
