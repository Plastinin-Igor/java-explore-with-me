package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventSearchParameters;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.EventSpecifications;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ParameterNotValidException;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<EventShortDto> getEventByUser(Long userId, int from, int size) {
        getUser(userId);
        PageRequest page = PageRequest.of(from, size, Sort.by("id").ascending());
        return eventRepository.findByInitiator_Id(userId, page)
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        checkDateTime(newEventDto.getEventDate());
        Event event = EventMapper.toEventFromNewEventDto(newEventDto);
        event.setInitiator(getUser(userId));
        event.setLocation(getLocation(LocationMapper.toLocation(newEventDto.getLocation())));
        event.setCreatedOn(LocalDateTime.now());
        event.setRequestModeration(newEventDto.getRequestModeration() != null ? newEventDto.getRequestModeration() : true);
        event.setState(State.PENDING);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventByUserAndById(Long userId, Long eventId) {
        getUser(userId);
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId);
        if (event == null) {
            log.error("Событие с id {} и пользователем {} не найдено в системе.", eventId, userId);
            throw new NotFoundException("Событие с id " + eventId + " и пользователем "
                                        + userId + " не найдено в системе");
        }

        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        User user = getUser(userId);
        Event oldEvent = getEvent(eventId);
        if (oldEvent.getState().equals(State.PUBLISHED)) {
            throw new DataConflictException("Изменить можно только отмененные события или события в состоянии " +
                                            "ожидания модерации.");
        }

        if (!userId.equals(user.getId())) {
            throw new DataConflictException("Изменить можно только событие, созданное текущим пользователем.");
        }

        Event newevent = EventMapper.toEventFromUpdateDto(updateEventUserRequest, oldEvent);
        checkDateTime(newevent.getEventDate());

        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                newevent.setPublishedOn(LocalDateTime.now());
                newevent.setState(State.CANCELED);
            } else if (updateEventUserRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                newevent.setPublishedOn(LocalDateTime.now());
                newevent.setState(State.PENDING);
            }
        }
        return EventMapper.toEventFullDto(eventRepository.save(newevent));
    }

    @Override
    public List<EventShortDto> getEventsByFilterSearch(EventSearchParameters params) {
        List<Specification<Event>> specifications = new ArrayList<>();

        // Текст
        if (params.getText() != null && !params.getText().isBlank()) {
            specifications.add(EventSpecifications.withText(params.getText()));
        }
        // Категории
        if (params.getCategories() != null && params.getCategories().isEmpty()) {
            specifications.add(EventSpecifications.inCategories(params.getCategories()));
        }
        // Платные/бесплатные события
        if (params.getPaid() != null) {
            specifications.add(EventSpecifications.isPaid(params.getPaid()));
        }
        // Период
        if (params.getRangeStart() != null && params.getRangeEnd() != null) {
            specifications.add(EventSpecifications.betweenPeriod(params.getRangeStart(), params.getRangeEnd()));
        } else {
            specifications.add(EventSpecifications.laterCurrentDateTime(LocalDateTime.now()));
        }
        // Только события у которых не исчерпан лимит запросов на участие
        if (params.getOnlyAvailable() != null && params.getOnlyAvailable()) {
            specifications.add(EventSpecifications.onlyAvailableEvent());
        }

        // Только опубликованные события


        // Режим сортировки
        Sort sort = Sort.unsorted();
        if (params.getSort() != null) {
            switch (params.getSort()) {
                case "EVENT_DATE" -> Sort.by(Sort.Direction.ASC, "eventDate");
                case "VIEWS" -> Sort.by(Sort.Direction.ASC, "viewsCount");
            }
            ;
        }

        Specification<Event> combinedSpecs = EventSpecifications.combine(specifications);
        Pageable paging = PageRequest.of(params.getFrom() / params.getSize(), params.getSize(), sort);
        return eventRepository.findAll(combinedSpecs, paging)
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден в системе."));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено в системе."));
    }

    private void checkDateTime(LocalDateTime newEventDate) {
        LocalDateTime twoHoursFromNow = LocalDateTime.now().plusHours(2);

        if (newEventDate.isBefore(twoHoursFromNow)) {
            String message = "Дата и время события не могут быть ранее, чем через два часа от текущего момента.";
            log.error(message);
            throw new ParameterNotValidException("EventDate", message);
        }
    }

    private Location getLocation(Location location) {
        Location locationFind = locationRepository.findByLatAndLon(location.getLat(), location.getLon());
        if (locationFind == null) {
            locationFind = locationRepository.save(location);
        }
        return locationFind;
    }

}
