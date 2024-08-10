package com.backend.komeet.post.model.dtos;

import com.backend.komeet.post.enums.Categories;
import com.backend.komeet.post.enums.PostStatus;
import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.post.model.entities.metadata.PostMetaData;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private Long commentCount;
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
        PostMetaData postMetaData = post.getPostMetaData();
        return PostDto.builder()
                .seq(post.getSeq())
                .title(postMetaData.getTitle())
                .content(postMetaData.getContent())
                .userSeq(post.getUser().getSeq())
                .userProfileUrl(post.getUser().getImageUrl())
                .userNickName(post.getUser().getNickName())
                .country(post.getUser().getCountry().getCountryName())
                .region(post.getUser().getRegion())
                .viewCount(postMetaData.getViewCount())
                .likeCount(postMetaData.getLikeCount())
                .commentCount(post.getCommentCount())
                .comments(new ArrayList<>())
                .tags(postMetaData.getTags())
                .attachments(postMetaData.getAttachments())
                .bookmarkUsers(postMetaData.getBookmarkUsers())
                .likeUsers(postMetaData.getLikeUsers())
                .isPublic(post.getIsPublic())
                .status(postMetaData.getStatus())
                .category(post.getCategory())
                .createdAt(post.getCreatedAt().toString())
                .build();
    }
}
