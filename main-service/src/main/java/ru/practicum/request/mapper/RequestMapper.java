package ru.practicum.request.mapper;

import ru.practicum.event.model.Event;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

public final class RequestMapper {

    public static Request toRequest(ParticipationRequestDto requestDto) {
        Request request = new Request();
        request.setId(requestDto.getId());
        request.setCreated(requestDto.getCreated());
        request.setStatus(requestDto.getStatus());

        if (requestDto.getEvent() != null) {
            Event event = new Event();
            event.setId(requestDto.getEvent());
            request.setEvent(event);
        }

        if (requestDto.getRequester() != null) {
            User user = new User();
            user.setId(requestDto.getRequester());
            request.setRequester(user);
        }

        return request;
    }

    public static ParticipationRequestDto toRequestDto(Request request) {
        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(request.getId());
        requestDto.setCreated(request.getCreated());
        requestDto.setEvent(request.getEvent().getId());
        requestDto.setRequester(request.getRequester().getId());
        requestDto.setStatus(request.getStatus());

        return requestDto;
    }
}
