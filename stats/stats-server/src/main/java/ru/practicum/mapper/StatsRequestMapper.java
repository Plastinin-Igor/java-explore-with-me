package ru.practicum.mapper;

import ru.practicum.RequestCreateDto;
import ru.practicum.RequestDto;
import ru.practicum.RequestOutputDto;
import ru.practicum.model.Request;

public final class StatsRequestMapper {

    public static Request toRequestFromCreate(RequestCreateDto requestCreateDto) {
        Request request = new Request();
        request.setApp(requestCreateDto.getApp());
        request.setUri(requestCreateDto.getUri());
        request.setIp(requestCreateDto.getIp());
        request.setTimestamp(requestCreateDto.getTimestamp());
        return request;
    }

    public static RequestDto toRequestDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setApp(request.getApp());
        requestDto.setUri(request.getUri());
        requestDto.setIp(request.getIp());
        requestDto.setTimestamp(request.getTimestamp());
        return requestDto;
    }

    public static RequestOutputDto toRequestOutputDto(Request request) {
        RequestOutputDto requestOutputDto = new RequestOutputDto();
        requestOutputDto.setApp(request.getApp());
        requestOutputDto.setUri(request.getUri());
        return requestOutputDto;
    }

}
