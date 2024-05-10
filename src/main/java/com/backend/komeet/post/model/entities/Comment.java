package com.backend.komeet.post.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import com.backend.komeet.post.enums.PostStatus;
import com.backend.komeet.user.model.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.backend.komeet.post.enums.PostStatus.*;

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
    @JsonIgnore
    private Post post;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Long> likeUsers;

    @Setter
    private int upVotes;
    private int downVotes;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    /**
     * 댓글 팩토리 메서드
     *
     * @param user    작성자
     * @param post    게시글
     * @param content 내용
     * @return Comment
     */
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
