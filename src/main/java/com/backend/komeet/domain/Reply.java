package com.backend.komeet.domain;

import com.backend.komeet.enums.PostStatus;
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

    @ManyToOne
    private User author;

    private String content;

    @ManyToOne
    @JoinColumn(name = "comment_seq")
    @JsonIgnore
    private Comment comment;

    @Setter
    private Integer upVotes;

    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Long> likeUsers;

    private Integer downVotes;

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
