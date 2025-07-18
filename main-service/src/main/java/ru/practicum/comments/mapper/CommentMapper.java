package ru.practicum.comments.mapper;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.StateComment;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

public final class CommentMapper {

    public static Comment toCommentFromNewtDto(NewCommentDto newCommentDto, User user, Event event) {
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());

        comment.setCreated(LocalDateTime.now());
        comment.setState(StateComment.PENDING);
        comment.setLikes(0L);
        comment.setDislikes(0L);
        comment.setAuthor(user);
        comment.setEvent(event);

        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setEvent(EventMapper.toEventShortDto(comment.getEvent()));
        commentDto.setAuthor(UserMapper.toUserShortDto(comment.getAuthor()));
        commentDto.setCreated(comment.getCreated());
        commentDto.setText(comment.getText());
        commentDto.setState(comment.getState());
        commentDto.setLikes(comment.getLikes());
        commentDto.setDislikes(comment.getDislikes());
        return commentDto;
    }

    public static Comment toCommentFromUpdate(UpdateCommentDto updateComment, Comment comment) {

        if (updateComment.hasText()) {
            comment.setText(updateComment.getText());
        }
        return comment;
    }

}
