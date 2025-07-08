package ru.practicum.event.mapper;

import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.model.Event;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.user.mapper.UserMapper;

public final class EventMapper {

    public static EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(event.getCategory() != null ? CategoryMapper.toCategoryDto(event.getCategory()) : null);
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setId(event.getId());
        eventFullDto.setInitiator(event.getInitiator() != null ? UserMapper.toUserShortDto(event.getInitiator()) : null);
        eventFullDto.setLocation(event.getLocation() != null ? LocationMapper.toLocationDto(event.getLocation()) : null);
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(event.getViews());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        return eventFullDto;
    }

    public static EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(event.getCategory() != null ? CategoryMapper.toCategoryDto(event.getCategory()) : null);
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setId(event.getId());
        eventShortDto.setInitiator(event.getInitiator() != null ? UserMapper.toUserShortDto(event.getInitiator()) : null);
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setViews(event.getViews());
        return eventShortDto;
    }


    public static Event toEventFromNewEventDto(NewEventDto newEventDto) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());

        if (newEventDto.getCategory() != null) {
            Category category = new Category();
            category.setId(newEventDto.getCategory());
            event.setCategory(category);
        } else {
            event.setCategory(null);
        }

        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setLocation(newEventDto.getLocation() != null ? LocationMapper.toLocation(newEventDto.getLocation()) : null);
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setTitle(newEventDto.getTitle());
        return event;
    }


    public static Event toEventFromUpdateDto(UpdateEventUserRequest updateEvent, Event event) {

        if (updateEvent.hasAnnotation()) {
            event.setAnnotation(updateEvent.getAnnotation());
        }

        if (updateEvent.hasCategory()) {
            Category category = new Category();
            category.setId(updateEvent.getCategory());
            event.setCategory(category);
        }

        if (updateEvent.hasDescription()) {
            event.setDescription(updateEvent.getDescription());
        }

        if (updateEvent.hasEventDate()) {
            event.setEventDate(updateEvent.getEventDate());
        }

        if (updateEvent.hasLocation()) {
            event.setLocation(LocationMapper.toLocation(updateEvent.getLocation()));
        }

        if (updateEvent.hasParticipantLimit()) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }

        if (updateEvent.hasTitle()) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }

        return event;
    }


}
