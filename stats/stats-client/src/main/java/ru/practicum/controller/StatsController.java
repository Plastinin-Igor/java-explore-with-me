package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.RequestCreateDto;
import ru.practicum.client.StatsClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final StatsClient statsClient;


    @PostMapping("/hit")
    public ResponseEntity<Object> addRequest(@RequestBody @Valid RequestCreateDto requestCreateDto) {
        log.info("Получен запрос на добавление статистики");
        return statsClient.addRequest(requestCreateDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStatsRequest(@RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                  @RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                  @RequestParam List<String> uris,
                                                  @RequestParam Boolean unique) {
        if (uris == null || uris.isEmpty()) {
            uris = Collections.emptyList();
        }
        if (unique == null) {
            unique = false;
        }

        log.info("Получен запрос получения статистики по посещениям с параметрами: start: {}; end: {}, uris: {}, unique: {}",
                start, end, uris, unique);
        return statsClient.getStatsRequest(start, end, uris, unique);
    }
}
