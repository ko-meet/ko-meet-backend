package com.backend.komeet.post.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import com.backend.komeet.post.enums.PostStatus;
import com.backend.komeet.post.model.entities.metadata.CommentMetaData;
import com.backend.komeet.user.model.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;

/**
 * 대댓글 엔티티
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Entity
public class Reply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_seq")
    @JsonIgnore
    private Comment comment;

    @Embedded
    private CommentMetaData commentMetaData;

    /**
     * 대댓글 팩토리 메서드
     */
    public static Reply from(
            User user,
            Comment comment,
            String content
    ) {
        return Reply.builder()
                .user(user)
                .comment(comment)
                .commentMetaData(
                        CommentMetaData.builder()
                                .content(content)
                                .upVotes(0)
                                .downVotes(0)
                                .likeUsers(new ArrayList<>())
                                .status(PostStatus.NORMAL)
                                .build()
                )
                .build();
    }
}
