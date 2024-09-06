package com.backend.immilog.post.infrastructure.jpa.repository;

import com.backend.immilog.post.infrastructure.jpa.entities.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {
}
