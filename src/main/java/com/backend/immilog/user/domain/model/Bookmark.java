package com.backend.immilog.user.domain.model;

import com.backend.immilog.user.domain.model.enums.BookmarkType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Bookmark(
        Long bookmarkSeq,
        BookmarkType bookmarkType,
        Long userSeq,
        Long postSeq,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static Bookmark of(
            Long userSeq,
            Long postSeq,
            BookmarkType bookmarkType
    ) {
        return Bookmark.builder()
                .userSeq(userSeq)
                .postSeq(postSeq)
                .bookmarkType(bookmarkType)
                .build();
    }
}
