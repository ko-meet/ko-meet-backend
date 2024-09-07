package com.backend.immilog.post.application.result;

import com.backend.immilog.post.domain.enums.Categories;
import com.backend.immilog.post.domain.enums.PostStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record PostResult(
        Long seq,
        String title,
        String content,
        Long userSeq,
        String userProfileUrl,
        String userNickName,
        List<CommentResult> comments,
        Long commentCount,
        Long viewCount,
        Long likeCount,
        List<String> tags,
        List<String> attachments,
        List<Long> likeUsers,
        List<Long> bookmarkUsers,
        String isPublic,
        String country,
        String region,
        Categories category,
        PostStatus status,
        String createdAt
) {
    public PostResult copyWithNewComments(
            List<CommentResult> comments
    ) {
        return PostResult.builder()
                .seq(this.seq())
                .title(this.title())
                .content(this.content())
                .userSeq(this.userSeq())
                .userProfileUrl(this.userProfileUrl())
                .userNickName(this.userNickName())
                .comments(comments)
                .commentCount(this.commentCount())
                .viewCount(this.viewCount())
                .likeCount(this.likeCount())
                .tags(this.tags())
                .attachments(this.attachments())
                .likeUsers(this.likeUsers())
                .bookmarkUsers(this.bookmarkUsers())
                .isPublic(this.isPublic())
                .country(this.country())
                .region(this.region())
                .category(this.category())
                .status(this.status())
                .createdAt(this.createdAt())
                .build();

    }
}
