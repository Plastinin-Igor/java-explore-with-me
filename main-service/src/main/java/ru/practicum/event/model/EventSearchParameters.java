package ru.practicum.event.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@ToString
@Getter
public class EventSearchParameters {

    private String text; // текст для поиска в содержимом аннотации и подробном описании события
    private List<Long> categories; // список идентификаторов категорий в которых будет вестись поиск
    private Boolean paid; // поиск только платных/бесплатных событий
    private LocalDateTime rangeStart; // дата и время не позже которых должно произойти событие
    private LocalDateTime rangeEnd; // дата и время не позже которых должно произойти событие
    private Boolean onlyAvailable; // только события у которых не исчерпан лимит запросов на участие
    private String sort; // Вариант сортировки: по дате события или по количеству просмотров:  EVENT_DATE, VIEWS
    private int from; // количество событий, которые нужно пропустить для формирования текущего набора
    private int size; // количество событий в наборе

}
