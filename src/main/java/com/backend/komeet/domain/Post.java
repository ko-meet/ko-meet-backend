package com.backend.komeet.domain;

import com.backend.komeet.dto.request.PostUploadRequest;
import com.backend.komeet.enums.Countries;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시물 엔티티
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Entity
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private String title;

    private String content;

    @ManyToOne(targetEntity = User.class)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    private Long viewCount;

    private Long likeCount;

    @ElementCollection
    private List<String> tags;

    @ElementCollection
    private List<String> attachments;

    private String isPublic;

    private String isActive;

    private Countries country;

    private String region;

    public static Post from(PostUploadRequest postUploadRequest, User user) {
        return Post.builder()
                .title(postUploadRequest.getTitle())
                .content(postUploadRequest.getContent())
                .tags(postUploadRequest.getTags())
                .attachments(postUploadRequest.getAttachments())
                .isPublic(postUploadRequest.getIsPublic() ? "Y" : "N")
                .isActive("Y")
                .country(user.getCountry())
                .region(user.getRegion())
                .likeCount(0L)
                .viewCount(0L)
                .user(user)
                .build();
    }
}
