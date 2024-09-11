package com.backend.immilog.post.domain.model;

import com.backend.immilog.post.domain.model.enums.PostType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Bookmark(
        Long bookmarkSeq,
        PostType postType,
        Long userSeq,
        Long postSeq,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static Bookmark of(
            Long userSeq,
            Long postSeq,
            PostType postType
    ) {
        return Bookmark.builder()
                .userSeq(userSeq)
                .postSeq(postSeq)
                .postType(postType)
                .build();
    }
}
