package com.backend.komeet.domain;

import com.backend.komeet.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.backend.komeet.enums.PostStatus.*;

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

    @ManyToOne
    private User user;

    private String content;

    @ManyToOne
    @JoinColumn(name = "post_seq")
    private Post post;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    private int upVotes;
    private int downVotes;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    public static Comment from(User user, Post post, String content) {
        return Comment.builder()
                .user(user)
                .post(post)
                .content(content)
                .upVotes(0)
                .downVotes(0)
                .replies(new ArrayList<>())
                .status(NORMAL)
                .build();
    }
}
