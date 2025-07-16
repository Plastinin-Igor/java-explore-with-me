package ru.practicum.comments.service;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.LikeDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;
import ru.practicum.comments.model.StateComment;

import java.util.List;

public interface CommentService {

    // Добавление комментария
    CommentDto addComment(Long userId, NewCommentDto newCommentDto);

    // Исправление комментария
    CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto);

    // Изменение состояния комментария
    CommentDto changeStateComment(Long userId, Long commentId, StateComment stateComment);

    // Изменение администратором состояния комментария
    CommentDto adminChangeStateComment(Long userId, Long commentId, StateComment stateComment);

    // Поставить лай/дизлайк комментарию
    CommentDto likeComment(Long userId, Long commentId, LikeDto likeDto);

    // Получить комментарий пользователя по Id
    CommentDto getCommentByUserAndId(Long userId, Long commentId);

    // Получить комментарий по Id
    CommentDto getCommentById(Long commentId);

    // Получить все опубликованные комментарии по событию
    List<CommentDto> getCommentByEvent(Long event, int from, int size, String sortMode);

    // Получить все опубликованные комментарии пользователя
    List<CommentDto> getCommentByAuthor(Long userId, int from, int size, String sortMode);


}
