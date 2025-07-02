package ru.practicum.service;

import ru.practicum.RequestCreateDto;
import ru.practicum.RequestDto;
import ru.practicum.RequestOutputDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    RequestDto addRequest(RequestCreateDto requestCreateDto);

    List<RequestOutputDto> getStatsRequest(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
