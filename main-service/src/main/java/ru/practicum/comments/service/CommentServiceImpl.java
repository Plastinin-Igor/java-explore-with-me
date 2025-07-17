package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.LikeDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.CommentLike;
import ru.practicum.comments.model.StateComment;
import ru.practicum.comments.repository.CommentLikeRepository;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentLikeRepository likeRepository;

    @Override
    public CommentDto addComment(Long userId, NewCommentDto newCommentDto) {
        User user = getUser(userId);
        Event event = getEvent(newCommentDto.getEvent());
        Comment comment = CommentMapper.toCommentFromNewtDto(newCommentDto);

        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());
        comment.setState(StateComment.PENDING);
        comment.setLikes(0L);
        comment.setDislikes(0L);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        User user = getUser(userId);
        Comment oldComment = getComment(commentId);

        if (!user.getId().equals(oldComment.getAuthor().getId())) {
            log.error("Исправлять комментарйи может только автор.");
            throw new DataConflictException("Исправлять комментарйи может только автор.");
        }

        Comment newComment = CommentMapper.toCommentFromUpdate(updateCommentDto, oldComment);

        // Если комментарий уже опубликован, то допишем в текст дату изменения
        if (oldComment.getState().equals(StateComment.PUBLISHED)) {
            newComment.setText(newComment.getText() + "\nИзменено " + LocalDateTime.now());
        }

        return CommentMapper.toCommentDto(commentRepository.save(newComment));
    }

    @Override
    public CommentDto changeStateComment(Long userId, Long commentId, StateComment stateComment) {
        Comment comment = getComment(commentId);

        if (!comment.getAuthor().getId().equals(userId)) {
            log.error("Изменить статус комментария может автор или администратор системы.");
            throw new DataConflictException("Изменить статус комментария может автор или администратор системы.");
        }
        comment.setState(stateComment);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto adminChangeStateComment(Long userId, Long commentId, StateComment stateComment) {
        Comment comment = getComment(commentId);

        comment.setState(stateComment);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto likeComment(Long userId, Long commentId, LikeDto likeDto) {

        User user = getUser(userId); // Лайки ставит, только зарегистрированный пользователь

        Comment comment = getComment(commentId);
        Long like = comment.getLikes();
        Long dislike = comment.getDislikes();

        CommentLike commentLike = likeRepository.findByComment_IdAndUser_Id(commentId, userId);

        if (commentLike == null) {
            commentLike = new CommentLike();
            if (likeDto.getLike() != null && likeDto.getLike()) {
                like++;
                commentLike.setLikes(true);
            }

            if (likeDto.getDislike() != null && likeDto.getDislike()) {
                dislike++;
                commentLike.setDislike(true);
            }

            comment.setLikes(like);
            comment.setDislikes(dislike);


            commentLike.setUser(user);
            commentLike.setComment(comment);

            likeRepository.save(commentLike);

        } else {
            if (likeDto.getLike() != null && likeDto.getLike() && commentLike.getLikes() != null
                && !commentLike.getLikes()) {
                like++;
                commentLike.setLikes(true);
            }
            if (likeDto.getDislike() != null && likeDto.getDislike() && commentLike.getDislike() != null
                && !commentLike.getDislike()) {
                dislike++;
                commentLike.setDislike(true);
            }

            comment.setLikes(like);
            comment.setDislikes(dislike);
            likeRepository.save(commentLike);
        }

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        return CommentMapper.toCommentDto(getComment(commentId));
    }

    @Override
    public CommentDto getCommentByUserAndId(Long userId, Long commentId) {
        Comment comment = commentRepository.findByIdAndAuthor_Id(commentId, userId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id "
                                                         + commentId + " для пользователя с id " + userId
                                                         + " не найден в системе."));
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentByEvent(Long eventId, int from, int size, String sortMode) {

        Sort sort = Sort.unsorted();
        switch (sortMode) {
            case "CREATE" -> Sort.by("created");
            case "LIKES" -> Sort.by("likes");
            default -> Sort.by("id");
        }
        PageRequest page = PageRequest.of(from, size, sort.ascending());

        return commentRepository.findByEvent_Id(eventId, page)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getCommentByAuthor(Long userId, int from, int size, String sortMode) {

        Sort sort = Sort.unsorted();
        switch (sortMode) {
            case "CREATE" -> Sort.by("created");
            case "LIKES" -> Sort.by("likes");
            default -> Sort.by("id");
        }
        PageRequest page = PageRequest.of(from, size, sort.ascending());

        return commentRepository.findByAuthor_Id(userId, page)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден в системе."));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено в системе."));
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с id " + commentId + " не найден в системе."));
    }


}
