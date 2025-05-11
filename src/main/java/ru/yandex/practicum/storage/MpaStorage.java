package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.MpaDto;
import java.util.Optional;

public interface MpaStorage {

    boolean existsById(Integer id);

    Optional<MpaDto> getMpaById(Integer id);
}
