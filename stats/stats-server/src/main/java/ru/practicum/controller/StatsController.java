package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.RequestCreateDto;
import ru.practicum.RequestDto;
import ru.practicum.RequestOutputDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto addRequest(@RequestBody RequestCreateDto requestCreateDto) {
        log.info("Получен запрос на добавление статистики");
        RequestDto requestDto = statsService.addRequest(requestCreateDto);
        log.info("Добавлена информация о статистике посещения URI: {}", requestDto.getUri());
        return requestDto;
    }

    @GetMapping("/stats")
    public List<RequestOutputDto> getStatsRequest(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                  @RequestParam(required = false) List<String> uris,
                                                  @RequestParam(required = false) Boolean unique) {

        if (uris == null || uris.isEmpty()) {
            uris = Collections.emptyList();
        }
        if (unique == null) {
            unique = false;
        }

        log.info("Получен запрос получения статистики по посещениям с параметрами: start: {}; end: {}, uris: {}, unique: {}",
                start, end, uris, unique);
        return statsService.getStatsRequest(start, end, uris, unique);
    }

}
