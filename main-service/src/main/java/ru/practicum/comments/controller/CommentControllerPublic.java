package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentControllerPublic {

    private final CommentService commentService;

    // Получить комментарий по Id
    @GetMapping("/comments/{commentId}")
    public CommentDto getCommentById(@PathVariable Long commentId) {
        log.info("Получен запрос GET:/comments/{}", commentId);
        return commentService.getCommentById(commentId);
    }

    // Получить все опубликованные комментарии по событию
    @GetMapping("/comments/events/{eventId}")
    public List<CommentDto> getCommentByEvent(@PathVariable Long eventId,
                                              @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                              @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
                                              @RequestParam(name = "sortMode", defaultValue = "CREATE", required = false) String sortMode) {
        log.info("Получен запрос GET:/comments/events/{}.", eventId);
        return commentService.getCommentByEvent(eventId, from, size, sortMode);
    }

}
