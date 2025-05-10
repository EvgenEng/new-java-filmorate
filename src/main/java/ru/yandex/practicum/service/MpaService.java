package ru.yandex.practicum.service;

import ru.yandex.practicum.model.MpaDto;
import java.util.List;

public interface MpaService {

    MpaDto getMpaById(Integer id);

    boolean existsById(Integer id);

    List<MpaDto> getAllMpaRatings();
}
