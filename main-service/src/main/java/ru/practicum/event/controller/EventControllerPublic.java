package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.RequestCreateDto;
import ru.practicum.client.StatClient;
import ru.practicum.event.dto.EventFullDto;
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
    private final StatClient statClient;

    @GetMapping("/events")
    public List<EventShortDto> getEventsByFilterSearch(
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "categories", required = false) List<Long> categories,
            @RequestParam(name = "paid", required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(name = "onlyAvailable", required = false) Boolean onlyAvailable,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            HttpServletRequest request) {
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

        List<EventShortDto> events = eventService.getEventsByFilterSearch(parameters);

        log.info("Направлен запрос на сохранение данных в сервис статистики GET:/events.");
        RequestCreateDto requestCreateDto = new RequestCreateDto();
        requestCreateDto.setApp("main-service");
        requestCreateDto.setIp(request.getRemoteAddr());
        requestCreateDto.setUri(request.getRequestURI());
        requestCreateDto.setTimestamp(LocalDateTime.now());
        statClient.addRequest(requestCreateDto);

        return events;
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEventById(@PathVariable Long eventId, HttpServletRequest request) {

        log.info("Получен запрос GET: /events/{}", eventId);
        EventFullDto event = eventService.getEventById(eventId);

        log.info("Направлен запрос на сохранение данных в сервис статистики GET:/events/{}.", eventId);
        RequestCreateDto requestCreateDto = new RequestCreateDto();
        requestCreateDto.setApp("main-service");
        requestCreateDto.setIp(request.getRemoteAddr());
        requestCreateDto.setUri(request.getRequestURI());
        requestCreateDto.setTimestamp(LocalDateTime.now());
        statClient.addRequest(requestCreateDto);

        return event;
    }
}
