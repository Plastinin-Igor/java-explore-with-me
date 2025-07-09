package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.EventSearchParameters;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventControllerPublic {

    private final EventService eventService;

    @GetMapping("/events")
    public List<EventShortDto> getEventsByFilterSearch(@RequestParam(name = "text", required = false) String text,
                                                       @RequestParam(name = "categories", required = false) List<Long> categories,
                                                       @RequestParam(name = "paid", required = false) Boolean paid,
                                                       @RequestParam(name = "rangeStart", required = false)
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                       @RequestParam(name = "rangeEnd", required = false)
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                       @RequestParam(name = "onlyAvailable", required = false) Boolean onlyAvailable,
                                                       @RequestParam(name = "sort", required = false) String sort,
                                                       @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                                       @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        EventSearchParameters parameters = EventSearchParameters
                .builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable != null && onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();
        log.info("Получен запрос GET: /events c параметрами: {}", parameters);

        //TODO что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики

        return eventService.getEventsByFilterSearch(parameters);
    }
}
