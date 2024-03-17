package com.backend.komeet.repository;

import com.backend.komeet.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 댓글 관련 레포지토리
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
