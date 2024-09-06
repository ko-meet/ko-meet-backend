package com.backend.immilog.post.infrastructure.jpa.repository;

import com.backend.immilog.post.domain.model.PostResource;
import com.backend.immilog.post.infrastructure.jpa.entities.PostResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostResourceJpaRepository extends JpaRepository<PostResourceEntity, Long> {
}
