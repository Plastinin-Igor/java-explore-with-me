package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.RequestOutputDto;
import ru.practicum.client.StatClient;
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
    private final StatClient statClient;

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
        checkDateTime(newEventDto.getEventDate(), 2);
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
        checkDateTime(newevent.getEventDate(), 2);

        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                newevent.setPublishedOn(LocalDateTime.now());
                newevent.setState(State.CANCELED);
            } else if (updateEventUserRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                newevent.setPublishedOn(LocalDateTime.now());
                newevent.setState(State.PENDING);
            }
        }

        newevent.setLocation(getLocation(newevent.getLocation()));

        return EventMapper.toEventFullDto(eventRepository.save(newevent));
    }

    @Override
    public List<EventShortDto> getEventsByFilterSearch(EventSearchParameters params) {
        List<Specification<Event>> specifications = new ArrayList<>();

        if (params.getRangeStart() != null && params.getRangeEnd() != null
                && params.getRangeStart().isAfter(params.getRangeEnd())) {
            log.error("Дата RangeStart не может быть меньше даты getRangeEnd.");
            throw new ParameterNotValidException("Даты", "Дата RangeStart не может быть меньше даты getRangeEnd.");
        }

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
        specifications.add(EventSpecifications.onlyPublishedEvent(State.PUBLISHED));

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

        List<Event> events = eventRepository.findAll(combinedSpecs, paging);

        List<EventShortDto> results = new ArrayList<>();
        for (Event event : events) {
            results.add(EventMapper.toEventShortDto(eventViewsStats(event)));
        }

        return results;
    }

    @Override
    public List<EventFullDto> getEventsByFilterSearchForAdmin(EventSearchParameters params) {
        List<Specification<Event>> specifications = new ArrayList<>();

        // Пользователи
        if (params.getUsers() != null && !params.getUsers().isEmpty()) {
            specifications.add(EventSpecifications.inUsers(params.getUsers()));
        }
        // Состояния
        if (params.getStates() != null && !params.getStates().isEmpty()) {
            specifications.add(EventSpecifications.inStates(params.getStates()));
        }
        // Категории
        if (params.getCategories() != null && !params.getCategories().isEmpty()) {
            specifications.add(EventSpecifications.inCategories(params.getCategories()));
        }
        // Период
        if (params.getRangeStart() != null && params.getRangeEnd() != null) {
            specifications.add(EventSpecifications.betweenPeriod(params.getRangeStart(), params.getRangeEnd()));
        } else {
            specifications.add(EventSpecifications.laterCurrentDateTime(LocalDateTime.now()));
        }

        Sort sort = Sort.unsorted();
        Specification<Event> combinedSpecs = EventSpecifications.combine(specifications);
        Pageable paging = PageRequest.of(params.getFrom() / params.getSize(), params.getSize(), sort);

        return eventRepository.findAll(combinedSpecs, paging)
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventUserRequest updateEvent) {
        Event oldEvent = getEvent(eventId);
        Event newevent = EventMapper.toEventFromUpdateDto(updateEvent, oldEvent);

        Location location = getLocation(newevent.getLocation());
        newevent.setLocation(location);

        // дата начала изменяемого события должна быть не ранее чем за час от даты публикации
        checkDateTime(oldEvent.getEventDate(), 1);

        if (updateEvent.getStateAction() != null) {
            switch (updateEvent.getStateAction()) {
                case PUBLISH_EVENT: // событие можно публиковать, только если оно в состоянии ожидания публикации
                    if (oldEvent.getState().equals(State.PENDING)) {
                        newevent.setState(State.PUBLISHED);
                    } else {
                        log.error("Событие можно публиковать, только если оно в состоянии ожидания публикации.");
                        throw new DataConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации.");
                    }
                    break;
                case CANCEL_REVIEW: // событие можно отклонить, только если оно еще не опубликовано
                    if (!oldEvent.getState().equals(State.PUBLISHED)) {
                        newevent.setState(State.CANCELED);
                    } else {
                        log.error("Событие можно отклонить, только если оно еще не опубликовано.");
                        throw new DataConflictException("Событие можно отклонить, только если оно еще не опубликовано.");
                    }
                    break;
                case REJECT_EVENT: // Отмена события
                    if (!oldEvent.getState().equals(State.PUBLISHED)) {
                        newevent.setState(State.CANCELED);
                    } else {
                        log.error("Событие можно отменить, только если оно еще не опубликовано.");
                        throw new DataConflictException("Событие можно отменить, только если оно еще не опубликовано.");
                    }
            }
        }
        return EventMapper.toEventFullDto(eventRepository.save(newevent));
    }

    @Override
    public EventFullDto getEventById(Long eventId) {

        Event event = getEvent(eventId);
        if (event.getState() != State.PUBLISHED) {
            log.error("Событие не опубликовано.");
            throw new NotFoundException("Событие не опубликовано.");
        }
        event = eventViewsStats(event);
        return EventMapper.toEventFullDto(event);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден в системе."));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено в системе."));
    }

    private void checkDateTime(LocalDateTime newEventDate, int noEarlierThanAnHour) {
        LocalDateTime hoursFromNow = LocalDateTime.now().plusHours(noEarlierThanAnHour);

        if (newEventDate.isBefore(hoursFromNow)) {
            String message = "Дата и время события не могут быть ранее, чем через "
                    + noEarlierThanAnHour + " час(a) от текущего момента.";
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

    private Event eventViewsStats(Event event) {
        ResponseEntity<List<RequestOutputDto>> response = statClient.getStatsRequest(
                LocalDateTime.now().minusYears(1),
                LocalDateTime.now().plusDays(1),
                List.of("/events/" + event.getId()),
                true
        );

        List<RequestOutputDto> viewStatsDto = response.getBody();

        if (viewStatsDto != null) {
            event.setViews(viewStatsDto.size());
        } else {
            event.setViews(0);
        }

        Event savedEvent = eventRepository.save(event);
        log.info("Сохранено событие с id {}, с подсчётом просмотров.", savedEvent.getId());
        return savedEvent;
    }

}
