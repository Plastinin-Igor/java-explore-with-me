package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.StatusRequest;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final EventService eventService;

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return requestRepository.findByRequester_Id(userId)
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addUserRequest(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        Request request = new Request();

        if (getRequestByUserIdAndEventId(userId, eventId) != null) {
            log.error("Нельзя добавить повторный запрос.");
            throw new DataConflictException("Нельзя добавить повторный запрос.");
        }

        if (userId.equals(event.getInitiator().getId())) {
            log.error("Инициатор события не может добавить запрос на участие в своём событии.");
            throw new DataConflictException("Инициатор события не может добавить запрос на участие в своём событии.");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            log.error("Нельзя участвовать в неопубликованном событии.");
            throw new DataConflictException("Нельзя участвовать в неопубликованном событии.");
        }

        if (event.getParticipantLimit() > 0
                && requestRepository.countByEvent_IdAndStatus(eventId, StatusRequest.CONFIRMED) >= event.getParticipantLimit()) {
            log.error("У события с id {} достигнут лимит запросов на участие.", eventId);
            throw new DataConflictException("У события с id " + eventId + "  достигнут лимит запросов на участие.");
        }

        if (!event.getRequestModeration()) {
            request.setStatus(StatusRequest.CONFIRMED);
        } else {
            request.setStatus(StatusRequest.PENDING);
        }

        if (event.getParticipantLimit() == 0) {
            request.setStatus(StatusRequest.CONFIRMED);
        }

        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);

        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        getUser(userId);
        getEvent(requestId);
        Request request = getRequestById(requestId);

        if (!request.getRequester().getId().equals(userId)) {
            log.error("Запрос пользователя с id {} для участие в событии с id {} не найден в системе.", userId, requestId);
            throw new NotFoundException("Запрос пользователя с id " + userId + " для участие в событии с id "
                    + requestId + " не найден в системе.");
        }
        request.setStatus(StatusRequest.CANCELED);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getUserRequestsByEvent(Long userId, Long eventId) {
        getUser(userId);
        Event event = getEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            log.error("Пользователь с id {} не является инициатором события с id {}.", userId, event);
            throw new DataConflictException("Пользователь с id " + userId
                    + " не является инициатором события с id " + event + ".");
        }
        return requestRepository.findByEvent_Id(eventId)
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeStatusRequest(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        getUser(userId);
        Event event = getEvent(eventId);
        List<Request> requests = requestRepository.findByEvent_Id(eventId);


        int participantLimit = event.getParticipantLimit(); // Лимит
        int quantityConfirmedRequest = event.getConfirmedRequests(); // Кол-во согласованных заявок

        // Результаты
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        // нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие
        if (quantityConfirmedRequest >= participantLimit) {
            log.error("Достигнут лимит по заявкам на событие с id {}.", event);
            throw new DataConflictException("Достигнут лимит по заявкам на событие с id " + event.getId());
        }

        if (updateRequest.getStatus().equals(StatusRequest.CONFIRMED)) {
            for (Request request : requests) {
                if (quantityConfirmedRequest < participantLimit) {
                    request.setStatus(StatusRequest.CONFIRMED);
                    confirmedRequests.add(RequestMapper.toRequestDto(request));
                    quantityConfirmedRequest++;
                    requestRepository.save(request);
                } else {
                    request.setStatus(StatusRequest.REJECTED);
                    rejectedRequests.add(RequestMapper.toRequestDto(request));
                    requestRepository.save(request);
                }
            }
        } else {
            for (Request request : requests) {
                request.setStatus(StatusRequest.REJECTED);
                rejectedRequests.add(RequestMapper.toRequestDto(request));
                requestRepository.save(request);
            }
        }

        // Сохранить событие с обновленной информации о подтверждениях
        event.setConfirmedRequests(quantityConfirmedRequest);
        eventRepository.save(event);


        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);
        return result;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден в системе."));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено в системе."));
    }

    private Request getRequestByUserIdAndEventId(Long userId, Long eventId) {
        return requestRepository.findByRequester_IdAndEvent_Id(userId, eventId);
    }

    private Request getRequestById(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос на участие не найден в системе."));
    }

}
