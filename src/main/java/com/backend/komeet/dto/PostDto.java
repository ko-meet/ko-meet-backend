package com.backend.komeet.dto;

import com.backend.komeet.domain.Comment;
import com.backend.komeet.domain.Post;
import com.backend.komeet.enums.Categories;
import com.backend.komeet.enums.Countries;
import com.backend.komeet.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

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

    public static PostDto from(Post post) {
        return PostDto.builder()
                .seq(post.getSeq())
                .title(post.getTitle())
                .content(post.getContent())
                .userSeq(post.getUser().getSeq())
                .userProfileUrl(post.getUser().getImageUrl())
                .comments(
                        post.getComments()
                                .stream()
                                .map(CommentDto::from)
                                .collect(Collectors.toList())
                )
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
