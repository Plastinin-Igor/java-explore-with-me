package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RequestControllerPrivate {

    private final RequestService requestService;

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        log.info("Получен запрос GET: /users/{}/requests", userId);
        return requestService.getUserRequests(userId);
    }

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addUserRequest(@PathVariable Long userId,
                                                  @RequestParam Long eventId) {
        log.info("Получен запрос POST: /users/{}/requests с параметром eventId={}", userId, eventId);
        ParticipationRequestDto requestDto = requestService.addUserRequest(userId, eventId);
        log.info("Запрос на участие успешно добавлен: {}", requestDto);
        return requestDto;
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelUserRequest(@PathVariable Long userId,
                                                     @PathVariable Long requestId) {
        log.info("Получен запрос PATCH: /users/{}/requests/{}/cancel", userId, requestId);
        ParticipationRequestDto requestDto = requestService.cancelUserRequest(userId, requestId);
        log.info("Запрос на участие в событии успешно отменен: {}.", requestDto);
        return requestDto;
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getUserRequestsByEvent(@PathVariable Long userId,
                                                                @PathVariable Long eventId) {
        log.info("Получен запрос GET: /users/{}/events/{}/requests", userId, eventId);
        return requestService.getUserRequestsByEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeStatusRequest(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest requestsUpdate) {
        log.info("Получен запрос PATCH: /users/{}/events/{}/requests для изменения статуса заявок: {}.",
                userId, eventId, requestsUpdate);
        return requestService.changeStatusRequest(userId, eventId, requestsUpdate);
    }

}
