package com.backend.immilog.post.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteractionUserJpaRepository extends JpaRepository<InteractionUserEntity, Long> {
    List<InteractionUserEntity> findByPostSeq(Long postSeq);
}
