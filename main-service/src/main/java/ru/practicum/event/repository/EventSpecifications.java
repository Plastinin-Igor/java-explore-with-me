package ru.practicum.event.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.event.model.Event;
import jakarta.persistence.criteria.Predicate;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public final class EventSpecifications {

    // Список id пользователей, чьи события нужно найти
    public static Specification<Event> inUsers(final List<Long> users) {
        return (event, query, criteriaBuilder) ->
                event.join("initiator").get("id").in(users);
    }

    // Список состояний в которых находятся искомые события
    public static Specification<Event> inStates(final List<State> states) {
        return (event, query, criteriaBuilder) ->
                event.get("state").in(states);
    }

    // Текст для поиска в содержимом аннотации и подробном описании события
    public static Specification<Event> withText(final String text) {
        return (event, query, criteriaBuilder) -> {
            String lowerCaseText = text.toLowerCase();
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(event.get("annotation")), "%" + lowerCaseText + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(event.get("description")), "%" + lowerCaseText + "%")
            );
        };
    }

    // список идентификаторов категорий в которых будет вестись поиск
    public static Specification<Event> inCategories(final List<Long> categories) {
        return (event, query, criteriaBuilder) ->
                event.join("category").get("id").in(categories);
    }

    // поиск только платных/бесплатных событий
    public static Specification<Event> isPaid(final boolean paid) {
        return (event, query, criteriaBuilder) ->
                criteriaBuilder.equal(event.get("paid"), paid);
    }

    // поиск за период
    public static Specification<Event> betweenPeriod(LocalDateTime start, LocalDateTime end) {
        return (event, query, criteriaBuilder) ->
                criteriaBuilder.between(event.get("eventDate"), start, end);
    }

    // поиск позже текущей даты и времени
    public static Specification<Event> laterCurrentDateTime(LocalDateTime date) {
        return (event, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(event.get("eventDate"), date);
    }

    // только события у которых не исчерпан лимит запросов на участие
    public static Specification<Event> onlyAvailableEvent() {
        return (event, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.le(event.get("confirmedRequests"), event.get("participantLimit")),
                criteriaBuilder.le(event.get("participantLimit"), 0)
        );
    }

    // только опубликованные события
    public static Specification<Event> onlyPublishedEvent(State state) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), state);
    }

    public static Specification<Event> combine(List<Specification<Event>> specs) {
        return (event, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (Specification<Event> spec : specs) {
                Predicate predicate = spec.toPredicate(event, query, criteriaBuilder);
                if (predicate != null) {
                    predicates.add(predicate);
                }
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

}
