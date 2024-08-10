package com.backend.komeet.post.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import com.backend.komeet.post.enums.Categories;
import com.backend.komeet.post.model.entities.metadata.PostMetaData;
import com.backend.komeet.post.presentation.request.PostUploadRequest;
import com.backend.komeet.user.model.entities.User;
import lombok.*;
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

    @Embedded
    private PostMetaData postMetaData;

    @Setter
    private String isPublic;

    @Setter
    private Long commentCount;

    @Enumerated(EnumType.STRING)
    private Categories category;

    /**
     * 게시물 팩토리 메서드
     */
    public static Post from(
            PostUploadRequest postUploadRequest,
            User user
    ) {
        return Post.builder()
                .postMetaData(
                        PostMetaData.builder()
                                .title(postUploadRequest.getTitle())
                                .content(postUploadRequest.getContent())
                                .tags(postUploadRequest.getTags())
                                .attachments(postUploadRequest.getAttachments())
                                .country(user.getCountry())
                                .region(user.getRegion())
                                .likeCount(0L)
                                .viewCount(0L)
                                .status(NORMAL)
                                .build()
                )
                .isPublic(postUploadRequest.getIsPublic() ? "Y" : "N")
                .category(postUploadRequest.getCategory())
                .comments(new ArrayList<>())
                .commentCount(0L)
                .user(user)
                .build();
    }

    /**
     * 게시물 북마크 추가
     */
    public void addBookmarkPost(
            Bookmark bookmark,
            Long userSeq) {
        this.postMetaData.getBookmarkUsers().add(userSeq);
        this.bookmarklist.add(bookmark);
    }

    /**
     * 게시물 북마크 삭제
     */
    public void removeBookmarkPost(
            Bookmark bookmark,
            Long userSeq) {
        this.postMetaData.getBookmarkUsers().remove(userSeq);
        this.bookmarklist.remove(bookmark);
    }
}
