package com.backend.komeet.post.repositories;

import com.backend.komeet.post.model.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 댓글 관련 레포지토리
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
