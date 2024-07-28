package com.backend.komeet.post.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import com.backend.komeet.post.enums.PostStatus;
import com.backend.komeet.user.model.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

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
    private User author;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_seq")
    @JsonIgnore
    private Comment comment;

    @Setter
    private Integer upVotes;

    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Long> likeUsers;

    private Integer downVotes;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    /**
     * 대댓글 팩토리 메서드
     */
    public static Reply from(
            User user,
            Comment comment,
            String content
    ) {
        return Reply.builder()
                .author(user)
                .comment(comment)
                .content(content)
                .upVotes(0)
                .downVotes(0)
                .status(PostStatus.NORMAL)
                .build();
    }
}
