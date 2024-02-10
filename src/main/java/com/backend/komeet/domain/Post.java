package com.backend.komeet.domain;

import com.backend.komeet.dto.request.PostUploadRequest;
import com.backend.komeet.enums.Categories;
import com.backend.komeet.enums.PostStatus;
import com.backend.komeet.enums.Countries;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.backend.komeet.enums.PostStatus.*;

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

    @Setter
    private String title;

    @Setter
    private String content;

    @ManyToOne(targetEntity = User.class)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Setter
    private Long viewCount;

    @Setter
    private Long likeCount;

    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> tags;

    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> attachments;

    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Long> likeUsers;

    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Long> bookmarkUsers;

    @Setter
    private String isPublic;

    @Enumerated(EnumType.STRING)
    private Countries country;

    private String region;

    @Enumerated(EnumType.STRING)
    private Categories category;

    @Setter
    private PostStatus status;

    public static Post from(PostUploadRequest postUploadRequest, User user) {
        return Post.builder()
                .title(postUploadRequest.getTitle())
                .content(postUploadRequest.getContent())
                .tags(postUploadRequest.getTags())
                .attachments(postUploadRequest.getAttachments())
                .isPublic(postUploadRequest.getIsPublic() ? "Y" : "N")
                .country(user.getCountry())
                .region(user.getRegion())
                .category(postUploadRequest.getCategory())
                .comments(new ArrayList<>())
                .likeCount(0L)
                .viewCount(0L)
                .user(user)
                .status(NORMAL)
                .build();
    }
}
