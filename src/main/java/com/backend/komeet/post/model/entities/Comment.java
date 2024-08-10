package com.backend.komeet.post.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import com.backend.komeet.post.model.entities.metadata.CommentMetaData;
import com.backend.komeet.user.model.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.backend.komeet.post.enums.PostStatus.NORMAL;

/**
 * 댓글 엔티티
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Entity
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_seq")
    @JsonIgnore
    private Post post;

    @OneToMany(
            mappedBy = "comment",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Reply> replies = new ArrayList<>();

    @Setter
    private int replyCount;

    @Embedded
    private CommentMetaData commentMetaData;

    /**
     * 댓글 팩토리 메서드
     */
    public static Comment from(
            User user,
            Post post,
            String content
    ) {
        return Comment.builder()
                .user(user)
                .post(post)
                .commentMetaData(
                        CommentMetaData.builder()
                                .content(content)
                                .upVotes(0)
                                .downVotes(0)
                                .likeUsers(new ArrayList<>())
                                .status(NORMAL)
                                .build()
                )
                .replyCount(0)
                .replies(new ArrayList<>())
                .build();
    }
}
