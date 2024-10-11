package com.backend.immilog.post.infrastructure.jpa;

import com.backend.immilog.post.domain.model.PostResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostResourceJpaRepository extends JpaRepository<PostResourceEntity, Long> {
    List<PostResource> findAllByPostSeq(Long seq);
}

