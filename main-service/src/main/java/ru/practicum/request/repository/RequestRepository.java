package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Request findByRequester_IdAndEvent_Id(Long userId, Long eventId);

    List<Request> findByRequester_Id(Long userId);

    int countByEvent_Id(Long eventId);

    List<Request> findByEvent_Id(Long eventId);

}
