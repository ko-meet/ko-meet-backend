package com.backend.immilog.post.model.entities;

import com.backend.immilog.global.model.BaseDateEntity;
import com.backend.immilog.post.model.enums.PostStatus;
import com.backend.immilog.post.model.enums.ReferenceType;
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
public class Comment extends BaseDateEntity {

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

    public static Comment of(
            Long userSeq,
            Long postSeq,
            String content,
            ReferenceType referenceType
    ) {
        return Comment.builder()
                .userSeq(userSeq)
                .postSeq(postSeq)
                .content(content)
                .likeCount(0)
                .replyCount(0)
                .status(PostStatus.NORMAL)
                .referenceType(referenceType)
                .likeUsers(new ArrayList<>())
                .build();
    }

}

