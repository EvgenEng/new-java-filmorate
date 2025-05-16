package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.Genre;
import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    Optional<Genre> findById(Integer id);

    boolean existsById(Integer id);

    List<Genre> findAll();
}
