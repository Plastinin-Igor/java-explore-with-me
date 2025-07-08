package ru.practicum.request.dto;

import ru.practicum.request.model.StatusParticipationRequest;

import java.time.LocalDateTime;

public class ParticipationRequestDto {

    private LocalDateTime created;
    private Long event;
    private Long id;
    private Long requester;
    private StatusParticipationRequest status;

}
