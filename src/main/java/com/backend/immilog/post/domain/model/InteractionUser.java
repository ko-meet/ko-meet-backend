package com.backend.immilog.post.domain.model;

import com.backend.immilog.post.domain.enums.InteractionType;
import com.backend.immilog.post.domain.enums.PostType;
import lombok.Builder;

@Builder
public record InteractionUser(
        Long seq,
        Long postSeq,
        PostType postType,
        InteractionType interactionType,
        Long userSeq
) {
    public static InteractionUser of(
            Long postSeq,
            PostType postType,
            InteractionType interactionType,
            Long userSeq
    ) {
        return InteractionUser.builder()
                .postSeq(postSeq)
                .postType(postType)
                .interactionType(interactionType)
                .userSeq(userSeq)
                .build();
    }
}
