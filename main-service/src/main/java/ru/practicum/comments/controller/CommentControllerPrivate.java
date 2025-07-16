package ru.practicum.comments.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.LikeDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;
import ru.practicum.comments.model.StateComment;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentControllerPrivate {

    private final CommentService commentService;

    // Добавление комментария
    @PostMapping("/users/{userId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long userId,
                                 @RequestBody @Valid NewCommentDto newCommentDto) {

        log.info("Получен запрос POST: /users{}/comments на добавление комментария {}.", userId, newCommentDto);
        CommentDto commentDto = commentService.addComment(userId, newCommentDto);
        log.info("Комментарий успешно добавлен: {}.", commentDto);
        return commentDto;
    }

    // Исправление комментария
    @PatchMapping("/users/{userId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @RequestBody UpdateCommentDto updateCommentDto) {
        log.info("Получен запрос PATCH:/users/{}/comments/{} на исправление комментария: {}",
                userId, commentId, updateCommentDto);
        CommentDto commentDto = commentService.updateComment(userId, commentId, updateCommentDto);
        log.info("Комментарий успешно обновлен: {}", commentDto);
        return commentDto;
    }

    // Изменение состояния комментария
    @PatchMapping("/users/{userId}/comments/{commentId}/state")
    public CommentDto changeStateComment(@PathVariable Long userId,
                                         @PathVariable Long commentId,
                                         @RequestParam(name = "stateComment") StateComment stateComment) {
        log.info("Получен запрос PATCH:/users/{}/comments/{}/state на изменение состояния комментария с параметрами:" +
                 " stateComment={}.", userId, commentId, stateComment);
        CommentDto commentDto = commentService.changeStateComment(userId, commentId, stateComment, false);
        log.info("Статус успешно изменен: {}", commentDto);
        return commentDto;
    }

    // Поставить лай/дизлайк комментарию
    @PatchMapping("/users/{userId}/comments/{commentId}/like")
    public CommentDto likeComment(@PathVariable Long userId,
                                  @PathVariable Long commentId,
                                  @RequestBody @Valid LikeDto likeDto) {
        log.info("Получен запрос PATCH:/users/{}/comments/{}/like на добавление лайка/дизлайка: {}.",
                userId, commentId, likeDto);
        return commentService.likeComment(userId, commentId, likeDto);
    }

    // Получить комментарий пользователя по Id
    @GetMapping("/users/{userId}/comments/{commentId}")
    public CommentDto getCommentByUserAndId(@PathVariable Long userId,
                                            @PathVariable Long commentId) {
        log.info("Получен запрос GET:/users/{}/comments/{}", userId, commentId);
        return commentService.getCommentByUserAndId(userId, commentId);
    }

    // Получить все опубликованные комментарии пользователя
    @GetMapping("/users/{userId}/comments")
    public List<CommentDto> getCommentByAuthor(@PathVariable Long userId,
                                               @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                               @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
                                               @RequestParam(name = "sortMode", defaultValue = "CREATE", required = false) String sortMode) {
        log.info("Получен запрос GET:/users/{}/comments", userId);
        return commentService.getCommentByAuthor(userId, from, size, sortMode);
    }

}
