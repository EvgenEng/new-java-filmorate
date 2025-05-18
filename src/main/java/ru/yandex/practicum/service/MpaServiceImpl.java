package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.MpaDto;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.storage.MpaStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    @Override
    public MpaDto getMpaById(Integer id) {
        return mpaStorage.getMpaById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("MPA not found with id: " + id));
    }

    @Override
    public boolean existsById(Integer id) {
        return mpaStorage.existsById(id);
    }

    @Override
    public List<MpaDto> getAllMpaRatings() {
        return mpaStorage.getAllMpaRatings().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private MpaDto convertToDto(Mpa mpa) {
        return new MpaDto(mpa.getId(), mpa.getName(), mpa.getDescription());
    }
}
