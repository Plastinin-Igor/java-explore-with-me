package ru.practicum.comments.mapper;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;

public final class CommentMapper {

    public static Comment toCommentFromNewtDto(NewCommentDto newCommentDto) {
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());

        if (newCommentDto.getEvent() != null) {
            Event event = new Event();
            event.setId(newCommentDto.getEvent());
            comment.setEvent(event);
        }
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
