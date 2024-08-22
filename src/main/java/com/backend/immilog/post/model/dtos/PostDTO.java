package com.backend.immilog.post.model.dtos;

import com.backend.immilog.post.enums.Categories;
import com.backend.immilog.post.enums.InteractionType;
import com.backend.immilog.post.enums.PostStatus;
import com.backend.immilog.post.enums.ResourceType;
import com.backend.immilog.post.model.embeddables.PostMetaData;
import com.backend.immilog.post.model.embeddables.PostUserData;
import com.backend.immilog.post.model.entities.InteractionUser;
import com.backend.immilog.post.model.entities.Post;
import com.backend.immilog.post.model.entities.PostResource;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.backend.immilog.post.enums.InteractionType.BOOKMARK;
import static com.backend.immilog.post.enums.InteractionType.LIKE;
import static com.backend.immilog.post.enums.PostType.POST;
import static com.backend.immilog.post.enums.ResourceType.ATTACHMENT;
import static com.backend.immilog.post.enums.ResourceType.TAG;

@Getter
@Setter
@ToString
public class PostDTO {
    private Long seq;
    private String title;
    private String content;
    private Long userSeq;
    private String userProfileUrl;
    private String userNickName;
    private List<CommentDTO> comments;
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

    public PostDTO(
            Post post,
            List<InteractionUser> interactionUsers,
            List<PostResource> postResources
    ) {
        interactionUsers = interactionUsers != null ? interactionUsers : List.of();
        postResources = postResources != null ? postResources : List.of();
        PostMetaData postMetaData = post.getPostMetaData();
        PostUserData postUserData = post.getPostUserData();

        List<Long> likeUsers = getLongs(interactionUsers, post, LIKE);
        List<Long> bookmarkUsers = getLongs(interactionUsers, post, BOOKMARK);
        List<String> tags = getStrings(postResources, post, TAG);
        List<String> attachments = getStrings(postResources, post, ATTACHMENT);

        this.seq = post.getSeq();
        this.title = postMetaData.getTitle();
        this.content = postMetaData.getContent();
        this.userSeq = postUserData.getUserSeq();
        this.userProfileUrl = postUserData.getProfileImage();
        this.userNickName = postUserData.getNickname();
        this.country = postMetaData.getCountry().getCountryName();
        this.region = postMetaData.getRegion();
        this.viewCount = postMetaData.getViewCount();
        this.likeCount = postMetaData.getLikeCount();
        this.commentCount = post.getCommentCount();
        this.comments = new ArrayList<>();
        this.likeUsers = likeUsers;
        this.bookmarkUsers = bookmarkUsers;
        this.tags = tags;
        this.attachments = attachments;
        this.isPublic = post.getIsPublic();
        this.status = postMetaData.getStatus();
        this.category = post.getCategory();
        this.createdAt = post.getCreatedAt().toString();
    }

    private static List<String> getStrings(
            List<PostResource> postResources,
            Post post,
            ResourceType type
    ) {
        return postResources.stream()
                .filter(Objects::nonNull)
                .filter(c -> c.getResourceType().equals(type))
                .map(PostResource::getContent)
                .toList();
    }

    private static List<Long> getLongs(
            List<InteractionUser> interactionUsers,
            Post post,
            InteractionType type
    ) {
        return interactionUsers.stream()
                .filter(Objects::nonNull)
                .filter(c -> c.getInteractionType().equals(type))
                .map(InteractionUser::getUserSeq)
                .toList();
    }

}
