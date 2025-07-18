package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.model.EventSearchParameters;

import java.util.List;

public interface EventService {

    List<EventShortDto> getEventByUser(Long userId, int from, int size);

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByUserAndById(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventShortDto> getEventsByFilterSearch(EventSearchParameters parameters);

    EventFullDto getEventById(Long eventId);

    List<EventFullDto> getEventsByFilterSearchForAdmin(EventSearchParameters parameters);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventUserRequest updateEventUserRequest);

}
