package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.model.StateComment;
import ru.practicum.comments.service.CommentService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentControllerAdmin {

    private final CommentService commentService;

    // Изменение состояния комментария
    @PatchMapping("/admin/{userId}/comments/{commentId}/state")
    public CommentDto changeStateComment(@PathVariable Long userId,
                                         @PathVariable Long commentId,
                                         @RequestParam(name = "stateComment") StateComment stateComment) {
        log.info("Получен запрос PATCH:/admin/{}/comments/{}/state на изменение состояния комментария с параметрами:" +
                 " stateComment={}.", userId, commentId, stateComment);
        CommentDto commentDto = commentService.adminChangeStateComment(userId, commentId, stateComment);
        log.info("Статус успешно изменен: {}", commentDto);
        return commentDto;
    }
}
