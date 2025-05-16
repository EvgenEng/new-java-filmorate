package ru.yandex.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.MpaDto;
import ru.yandex.practicum.service.MpaService;
import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public List<MpaDto> getAllMpa() {
        log.info("Getting all MPA ratings");
        return mpaService.getAllMpaRatings();
    }

    @GetMapping("/{id}")
    public MpaDto getMpa(@PathVariable int id) {
        log.info("Getting MPA rating with ID: {}", id);
        return mpaService.getMpaById(id);
    }
}
