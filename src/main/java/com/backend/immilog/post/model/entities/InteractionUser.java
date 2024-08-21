package com.backend.immilog.post.model.entities;

import com.backend.immilog.post.enums.InteractionType;
import com.backend.immilog.post.enums.PostType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@DynamicUpdate
@Entity
public class InteractionUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private Long postSeq;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @Enumerated(EnumType.STRING)
    private InteractionType interactionType;

    private Long userSeq;

    public static InteractionUser of(
            Long postSeq,
            PostType postType,
            InteractionType interactionType,
            Long userSeq
    ) {
        return InteractionUser.builder()
                .postSeq(postSeq)
                .postType(postType)
                .interactionType(interactionType)
                .userSeq(userSeq)
                .build();
    }
}
