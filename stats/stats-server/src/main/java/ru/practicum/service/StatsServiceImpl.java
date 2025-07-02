package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.RequestCreateDto;
import ru.practicum.RequestDto;
import ru.practicum.RequestOutputDto;
import ru.practicum.mapper.StatsRequestMapper;
import ru.practicum.model.Request;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;

    @Override
    @Transactional
    public RequestDto addRequest(RequestCreateDto requestCreateDto) {
        Request request = StatsRequestMapper.toRequestFromCreate(requestCreateDto);
        return StatsRequestMapper.toRequestDto(repository.save(request));
    }

    @Override
    public List<RequestOutputDto> getStatsRequest(LocalDateTime start,
                                                  LocalDateTime end,
                                                  List<String> uris,
                                                  Boolean unique) {
        if (unique) {
            if (uris == null || uris.isEmpty()) {
                return repository.getUniqueRequestsByPeriod(start, end);
            } else {
                return repository.qetUniqueRequestByPeriodWithUris(start, end, uris);
            }
        } else {
            if (uris == null || uris.isEmpty()) {
                return repository.getAllRequestsByPeriod(start, end);
            } else {
                return repository.qetRequestByPeriodWithUris(start, end, uris);
            }
        }
    }
}
