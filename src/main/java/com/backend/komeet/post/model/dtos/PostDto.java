package com.backend.komeet.post.model.dtos;

import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.post.enums.Categories;
import com.backend.komeet.post.enums.PostStatus;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시물 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {
    private Long seq;
    private String title;
    private String content;
    private Long userSeq;
    private String userProfileUrl;
    private String userNickName;
    @Setter
    private List<CommentDto> comments;
    private Long viewCount;
    private Long likeCount;
    private List<String> tags;
    private List<String> attachments;
    private List<Long> likeUsers;
    private List<Long> bookmarkUsers;
    private String isPublic;
    private String country;
    private String region;
    private Categories category;
    private PostStatus status;
    private String createdAt;

    public static PostDto from(
            Post post
    ) {
        return PostDto.builder()
                .seq(post.getSeq())
                .title(post.getTitle())
                .content(post.getContent())
                .userSeq(post.getUser().getSeq())
                .userProfileUrl(post.getUser().getImageUrl())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .tags(post.getTags())
                .attachments(post.getAttachments())
                .bookmarkUsers(post.getBookmarkUsers())
                .likeUsers(post.getLikeUsers())
                .isPublic(post.getIsPublic())
                .country(post.getUser().getCountry().getCountryName())
                .status(post.getStatus())
                .region(post.getUser().getRegion())
                .category(post.getCategory())
                .userNickName(post.getUser().getNickName())
                .createdAt(post.getCreatedAt().toString())
                .build();
    }
}
