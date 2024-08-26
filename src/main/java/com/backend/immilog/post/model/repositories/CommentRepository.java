package com.backend.immilog.post.model.repositories;

import com.backend.immilog.post.model.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository
        extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
}
