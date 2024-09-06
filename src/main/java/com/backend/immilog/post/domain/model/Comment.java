package com.backend.immilog.post.domain.model;

import com.backend.immilog.post.domain.enums.PostStatus;
import com.backend.immilog.post.domain.enums.ReferenceType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
public record Comment(
        Long seq,
        Long userSeq,
        Long postSeq,
        Long parentSeq,
        int replyCount,
        Integer likeCount,
        String content,
        ReferenceType referenceType,
        PostStatus status,
        List<Long> likeUsers,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static Comment of(
            Long userSeq,
            Long postSeq,
            String content,
            ReferenceType referenceType
    ) {
        return Comment.builder()
                .userSeq(userSeq)
                .postSeq(postSeq)
                .content(content)
                .likeCount(0)
                .replyCount(0)
                .status(PostStatus.NORMAL)
                .referenceType(referenceType)
                .likeUsers(new ArrayList<>())
                .build();
    }

}

