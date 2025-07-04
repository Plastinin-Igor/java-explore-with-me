package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.RequestOutputDto;
import ru.practicum.model.Request;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Request, Long> {

    // Статистика посещений за период
    @Query(value = "SELECT new ru.practicum.RequestOutputDto(r.app, r.uri, COUNT(r.ip) hits)" +
                   "FROM Request r " +
                   "WHERE r.timestamp between ?1 and ?2 " +
                   "GROUP BY r.app, r.uri " +
                   "ORDER BY hits DESC ")
    List<RequestOutputDto> getAllRequestsByPeriod(LocalDateTime start, LocalDateTime end);

    // Статистика уникальных посещений за период
    @Query(value = "SELECT new ru.practicum.RequestOutputDto(r.app, r.uri, COUNT(DISTINCT r.ip) hits)" +
                   "FROM Request r " +
                   "WHERE r.timestamp between ?1 and ?2 " +
                   "GROUP BY r.app, r.uri " +
                   "ORDER BY hits DESC ")
    List<RequestOutputDto> getUniqueRequestsByPeriod(LocalDateTime start, LocalDateTime end);

    // Статистика посещений за период по списку uri
    @Query(value = "SELECT new ru.practicum.RequestOutputDto(r.app, r.uri, COUNT(r.ip) hits)" +
                   "FROM Request r " +
                   "WHERE r.timestamp between ?1 and ?2 " +
                   "  AND r.uri in ?3 " +
                   "GROUP BY r.app, r.uri " +
                   "ORDER BY hits DESC ")
    List<RequestOutputDto> qetRequestByPeriodWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    // Статистика уникальных посещений за период по списку uri
    @Query(value = "SELECT new ru.practicum.RequestOutputDto(r.app, r.uri, COUNT(DISTINCT r.ip) hits)" +
                   "FROM Request r " +
                   "WHERE r.timestamp between ?1 and ?2 " +
                   "  AND r.uri in ?3 " +
                   "GROUP BY r.app, r.uri " +
                   "ORDER BY hits DESC ")
    List<RequestOutputDto> qetUniqueRequestByPeriodWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

}