package com.backend.komeet.domain;

import com.backend.komeet.enums.CommentStatus;
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
    private Long seq;

    @ManyToOne
    private User author;

    private String content;

    @ManyToOne
    @JoinColumn(name = "comment_seq")
    private Comment comment;

    private int upVotes;

    private int downVotes;

    @Enumerated(EnumType.STRING)
    private CommentStatus status;
}
