package com.backend.komeet.post.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import com.backend.komeet.post.enums.Categories;
import com.backend.komeet.post.enums.PostStatus;
import com.backend.komeet.post.presentation.request.PostUploadRequest;
import com.backend.komeet.user.enums.Countries;
import com.backend.komeet.user.model.entities.User;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.backend.komeet.post.enums.PostStatus.NORMAL;

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

    @ManyToOne(
            targetEntity = User.class,
            fetch = FetchType.LAZY
    )
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "bookmark_post",
            joinColumns = @JoinColumn(name = "post_seq"),
            inverseJoinColumns = @JoinColumn(name = "bookmark_seq")
    )
    private List<Bookmark> bookmarklist = new ArrayList<>();

    @OneToMany(
            mappedBy = "post",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Comment> comments = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> tags;

    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> attachments;

    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Long> likeUsers;

    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Long> bookmarkUsers;

    @Setter
    private String isPublic;

    @Setter
    private Long commentCount;

    @Setter
    private Long viewCount;

    @Setter
    private Long likeCount;

    @Enumerated(EnumType.STRING)
    private Countries country;

    private String region;

    @Enumerated(EnumType.STRING)
    private Categories category;

    @Setter
    private PostStatus status;

    /**
     * 게시물 팩토리 메서드
     */
    public static Post from(
            PostUploadRequest postUploadRequest,
            User user
    ) {
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
                .commentCount(0L)
                .user(user)
                .status(NORMAL)
                .build();
    }

    /**
     * 게시물 북마크 추가
     */
    public void addBookmarkPost(
            Bookmark bookmark
    ) {
        this.bookmarkUsers.add(bookmark.getUserSeq());
        this.bookmarklist.add(bookmark);
    }

    /**
     * 게시물 북마크 삭제
     */
    public void removeBookmarkPost(
            Bookmark bookmark
    ) {
        this.bookmarkUsers.remove(bookmark.getUserSeq());
        this.bookmarklist.remove(bookmark);
    }
}
