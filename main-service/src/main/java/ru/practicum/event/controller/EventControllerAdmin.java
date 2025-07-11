package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.model.EventSearchParameters;
import ru.practicum.event.model.State;
import ru.practicum.event.service.EventService;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventControllerAdmin {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEventsByFilterSearchForAdmin(
            @RequestParam(name = "users", required = false) List<Long> users,
            @RequestParam(name = "states", required = false) List<State> states,
            @RequestParam(name = "categories", required = false) List<Long> categories,
            @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {

        EventSearchParameters parameters = EventSearchParameters
                .builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        log.info("Получен запрос GET: admin/events с параметрами: {}.", parameters);
        return eventService.getEventsByFilterSearchForAdmin(parameters);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                           @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("Получен запрос PATCH: admin/events/{} на исправление события: {}.", eventId, updateEventUserRequest);
        EventFullDto eventFullDto = eventService.updateEventByAdmin(eventId, updateEventUserRequest);
        log.info("Событие {} успешно обновлено в системе.", eventFullDto);
        return eventFullDto;
    }
}
