package com.backend.immilog.post.infrastructure.jpa;

import com.backend.immilog.post.domain.model.enums.InteractionType;
import com.backend.immilog.post.domain.model.enums.PostType;
import io.lettuce.core.Value;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InteractionUserJpaRepository extends JpaRepository<InteractionUserEntity, Long> {
    List<InteractionUserEntity> findByPostSeq(Long postSeq);

    Optional<InteractionUserEntity> findByPostSeqAndUserSeqAndPostTypeAndInteractionType(
            Long postSeq,
            Long userSeq,
            PostType postType,
            InteractionType interactionType
    );
}
