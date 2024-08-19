package com.backend.immilog.post.model.entities;

import com.backend.immilog.post.enums.PostType;
import com.backend.immilog.post.enums.ResourceType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@DynamicUpdate
@Entity
public class PostResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private Long postSeq;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    private String content;

    public static PostResource of(
            PostType postType,
            ResourceType resourceType,
            String content,
            Long postSeq
    ) {
        return PostResource.builder()
                .postType(postType)
                .resourceType(resourceType)
                .content(content)
                .postSeq(postSeq)
                .build();
    }
}
