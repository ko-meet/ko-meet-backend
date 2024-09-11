package com.backend.immilog.post.infrastructure.jpa.entities;

import com.backend.immilog.post.domain.model.InteractionUser;
import com.backend.immilog.post.domain.model.enums.InteractionType;
import com.backend.immilog.post.domain.model.enums.PostType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@DynamicUpdate
@Entity
@Table(name = "interaction_user")
public class InteractionUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private Long postSeq;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @Enumerated(EnumType.STRING)
    private InteractionType interactionType;

    private Long userSeq;

    public static InteractionUserEntity from(
            InteractionUser interactionUser
    ) {
        return InteractionUserEntity.builder()
                .postSeq(interactionUser.postSeq())
                .postType(interactionUser.postType())
                .interactionType(interactionUser.interactionType())
                .userSeq(interactionUser.userSeq())
                .build();
    }

    public InteractionUser toDomain() {
        return InteractionUser.builder()
                .postSeq(postSeq)
                .postType(postType)
                .interactionType(interactionType)
                .userSeq(userSeq)
                .build();
    }
}
