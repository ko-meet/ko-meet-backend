package com.backend.immilog.post.domain.repositories;

import com.backend.immilog.post.domain.model.InteractionUser;
import com.backend.immilog.post.domain.model.enums.InteractionType;
import com.backend.immilog.post.domain.model.enums.PostType;

import java.util.List;
import java.util.Optional;

public interface InteractionUserRepository {
    List<InteractionUser> getByPostSeq(
            Long postSeq
    );

    void deleteEntity(
            InteractionUser interactionUser
    );

    void saveEntity(
            InteractionUser likeUser
    );

    Optional<InteractionUser> getByPostSeqAndUserSeqAndPostTypeAndInteractionType(
            Long postSeq,
            Long userSeq,
            PostType postType,
            InteractionType interactionType
    );
}
