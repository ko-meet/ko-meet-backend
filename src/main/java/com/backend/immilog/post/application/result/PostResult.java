package com.backend.immilog.post.application.result;

import com.backend.immilog.post.domain.vo.PostMetaData;
import com.backend.immilog.post.domain.vo.PostUserData;
import com.backend.immilog.post.domain.model.InteractionUser;
import com.backend.immilog.post.domain.model.Post;
import com.backend.immilog.post.domain.model.PostResource;
import com.backend.immilog.post.domain.enums.Categories;
import com.backend.immilog.post.domain.enums.InteractionType;
import com.backend.immilog.post.domain.enums.PostStatus;
import com.backend.immilog.post.domain.enums.ResourceType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.backend.immilog.post.domain.enums.InteractionType.BOOKMARK;
import static com.backend.immilog.post.domain.enums.InteractionType.LIKE;
import static com.backend.immilog.post.domain.enums.ResourceType.ATTACHMENT;
import static com.backend.immilog.post.domain.enums.ResourceType.TAG;

@Getter
@Setter
@ToString
public class PostResult {
    private Long seq;
    private String title;
    private String content;
    private Long userSeq;
    private String userProfileUrl;
    private String userNickName;
    private List<CommentResult> comments;
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

    public PostResult(
            Post post,
            List<InteractionUser> interactionUsers,
            List<PostResource> postResources
    ) {
        interactionUsers = interactionUsers != null ? interactionUsers : List.of();
        postResources = postResources != null ? postResources : List.of();
        PostMetaData postMetaData = post.postMetaData();
        PostUserData postUserData = post.postUserData();

        List<Long> likeUsers = getLongs(interactionUsers, post, LIKE);
        List<Long> bookmarkUsers = getLongs(interactionUsers, post, BOOKMARK);
        List<String> tags = getStrings(postResources, post, TAG);
        List<String> attachments = getStrings(postResources, post, ATTACHMENT);

        this.seq = post.seq();
        this.title = postMetaData.getTitle();
        this.content = postMetaData.getContent();
        this.userSeq = postUserData.getUserSeq();
        this.userProfileUrl = postUserData.getProfileImage();
        this.userNickName = postUserData.getNickname();
        this.country = postMetaData.getCountry().getCountryName();
        this.region = postMetaData.getRegion();
        this.viewCount = postMetaData.getViewCount();
        this.likeCount = postMetaData.getLikeCount();
        this.commentCount = post.commentCount();
        this.comments = new ArrayList<>();
        this.likeUsers = likeUsers;
        this.bookmarkUsers = bookmarkUsers;
        this.tags = tags;
        this.attachments = attachments;
        this.isPublic = post.isPublic();
        this.status = postMetaData.getStatus();
        this.category = post.category();
        this.createdAt = post.createdAt().toString();
    }

    private static List<String> getStrings(
            List<PostResource> postResources,
            Post post,
            ResourceType type
    ) {
        return postResources.stream()
                .filter(Objects::nonNull)
                .filter(c -> c.resourceType().equals(type))
                .map(PostResource::content)
                .toList();
    }

    private static List<Long> getLongs(
            List<InteractionUser> interactionUsers,
            Post post,
            InteractionType type
    ) {
        return interactionUsers.stream()
                .filter(Objects::nonNull)
                .filter(c -> c.interactionType().equals(type))
                .map(InteractionUser::userSeq)
                .toList();
    }

}
