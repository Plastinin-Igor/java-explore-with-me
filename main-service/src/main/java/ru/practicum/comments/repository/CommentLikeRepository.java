package ru.practicum.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comments.model.CommentLike;


public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    CommentLike findByComment_IdAndUser_Id(Long commentId, Long userId);

}
