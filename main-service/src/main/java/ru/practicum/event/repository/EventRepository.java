package ru.practicum.event.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByInitiator_Id(Long userId, PageRequest page);

    Event findByIdAndInitiator_Id(Long eventId, Long userId);

    List<Event> findAll(Specification<Event> combinedSpecs, Pageable paging);
}
