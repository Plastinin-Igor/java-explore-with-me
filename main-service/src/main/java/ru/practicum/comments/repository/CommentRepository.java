package ru.practicum.comments.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comments.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByEvent_Id(Long eventId, PageRequest page);

    List<Comment> findByAuthor_Id(Long userId, PageRequest page);

    Optional<Comment> findByIdAndAuthor_Id(Long commentId, Long userId);
}
