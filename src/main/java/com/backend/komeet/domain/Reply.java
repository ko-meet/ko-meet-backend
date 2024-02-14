package com.backend.komeet.domain;

import com.backend.komeet.enums.PostStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * 대댓글 엔티티
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Entity
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @ManyToOne
    private User author;

    private String content;

    @ManyToOne
    @JoinColumn(name = "comment_seq")
    @JsonIgnore
    private Comment comment;

    private int upVotes;

    private int downVotes;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    public static Reply from(User user, Comment comment, String content) {
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
