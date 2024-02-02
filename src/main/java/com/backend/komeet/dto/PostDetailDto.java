package com.backend.komeet.dto;

import com.backend.komeet.domain.Comment;
import com.backend.komeet.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDetailDto {
    private Long seq;
    private String title;
    private String content;
    private String userNickname;
    private Long userSeq;
    private String userProfile;
    private List<Comment> comments;
    private Long viewCount;
    private Long likeCount;
    private List<String> tags;
    private List<String> attachments;
    private String country;
    private String region;
    private String category;
    private String status;
    private String createdAt;

    public static PostDetailDto from(Post post) {
        return PostDetailDto.builder()
                .seq(post.getSeq())
                .title(post.getTitle())
                .content(post.getContent())
                .userNickname(post.getUser().getNickName())
                .userSeq(post.getUser().getSeq())
                .userProfile(post.getUser().getImageUrl())
                .comments(post.getComments())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .tags(post.getTags())
                .attachments(post.getAttachments())
                .country(post.getCountry().getCountryName())
                .region(post.getRegion())
                .category(post.getCategory().getCategory())
                .status(post.getStatus().toString())
                .createdAt(post.getCreatedAt().toString())
                .build();
    }
}
