package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventControllerPrivate {

    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventByUser(@PathVariable Long userId,
                                              @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                              @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Получен запрос GET: /users/{}/events с параметрами: from={}, size={}.", userId, from, size);
        return eventService.getEventByUser(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Поступил запрос POST:  /users/{}/events на добавление события {}.", userId, newEventDto);
        EventFullDto eventFullDto = eventService.addEvent(userId, newEventDto);
        log.info("Событие {} успешно добавлено в систему.", eventFullDto);
        return eventFullDto;
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByUserAndById(@PathVariable Long userId,
                                              @PathVariable Long eventId) {
        log.info("Поступил запрос GET: users/{}/events/{}.", userId, eventId);
        return eventService.getEventByUserAndById(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("Поступил запрос PATCH: users/{}/events/{} на исправление события {}.", userId, eventId,
                updateEventUserRequest);
        EventFullDto eventFullDto = eventService.updateEventByUser(userId, eventId, updateEventUserRequest);
        log.info("Событие {} успешно обновлено в системе.", eventFullDto);
        return eventFullDto;
    }

}
