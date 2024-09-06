package com.backend.immilog.post.infrastructure.jpa.entities;

import com.backend.immilog.global.model.BaseDateEntity;
import com.backend.immilog.post.domain.model.Comment;
import com.backend.immilog.post.domain.enums.PostStatus;
import com.backend.immilog.post.domain.enums.ReferenceType;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Entity
@Table(name = "comment")
public class CommentEntity extends BaseDateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private Long userSeq;

    private Long postSeq;

    private Long parentSeq;

    @Setter
    private int replyCount;

    @Setter
    private Integer likeCount;

    private String content;

    @Enumerated(EnumType.STRING)
    private ReferenceType referenceType;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(value = CascadeType.ALL)
    private List<Long> likeUsers = new ArrayList<>();

    public static CommentEntity from(
            Comment comment
    ) {
        return CommentEntity.builder()
                .seq(comment.seq())
                .userSeq(comment.userSeq())
                .postSeq(comment.postSeq())
                .parentSeq(comment.parentSeq())
                .replyCount(comment.replyCount())
                .likeCount(comment.likeCount())
                .content(comment.content())
                .referenceType(comment.referenceType())
                .status(comment.status())
                .likeUsers(comment.likeUsers())
                .build();
    }

    public Comment toDomain() {
        return Comment.builder()
                .seq(this.seq)
                .userSeq(this.userSeq)
                .postSeq(this.postSeq)
                .parentSeq(this.parentSeq)
                .replyCount(this.replyCount)
                .likeCount(this.likeCount)
                .content(this.content)
                .referenceType(this.referenceType)
                .status(this.status)
                .likeUsers(this.likeUsers)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

}

