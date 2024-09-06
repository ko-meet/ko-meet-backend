package com.backend.immilog.post.infrastructure.jpa.repository;

import com.backend.immilog.post.infrastructure.jpa.entities.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {
}
