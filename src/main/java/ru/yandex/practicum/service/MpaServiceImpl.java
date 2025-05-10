package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.MpaDto;
import ru.yandex.practicum.model.MpaRating;
import ru.yandex.practicum.storage.MpaStorage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    @Override
    public MpaDto getMpaById(Integer id) {
        log.debug("Getting MPA by ID: {}", id);
        return mpaStorage.getMpaById(id)
                .orElseThrow(() -> {
                    log.error("MPA with ID {} not found", id);
                    return new NotFoundException("MPA not found with id: " + id);
                });
    }

    @Override
    public boolean existsById(Integer id) {
        if (id == null) {
            log.warn("Null MPA ID checked for existence");
            return false;
        }
        boolean exists = mpaStorage.existsById(id);
        log.debug("Checked existence for MPA ID {}: {}", id, exists);
        return exists;
    }

    @Override
    public List<MpaDto> getAllMpaRatings() {
        log.debug("Getting all MPA ratings");
        return Stream.of(MpaRating.values())
                .map(rating -> new MpaDto(rating.getId(), rating.getName()))
                .collect(Collectors.toList());
    }
}
