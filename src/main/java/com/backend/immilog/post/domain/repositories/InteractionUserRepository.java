package com.backend.immilog.post.domain.repositories;

import com.backend.immilog.post.domain.model.InteractionUser;

import java.util.List;

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
}
