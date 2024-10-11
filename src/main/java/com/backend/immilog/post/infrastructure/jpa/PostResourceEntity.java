package com.backend.immilog.post.infrastructure.jpa;

import com.backend.immilog.post.domain.model.PostResource;
import com.backend.immilog.post.domain.model.enums.PostType;
import com.backend.immilog.post.domain.model.enums.ResourceType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@DynamicUpdate
@Entity
@Table(name = "post_resource")
public class PostResourceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private Long postSeq;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    private String content;

    public static PostResourceEntity from(
           PostResource postResource
    ) {
        return PostResourceEntity.builder()
                .postSeq(postResource.postSeq())
                .postType(postResource.postType())
                .resourceType(postResource.resourceType())
                .content(postResource.content())
                .build();
    }

    public PostResource toDomain() {
        return PostResource.builder()
                .postSeq(postSeq)
                .postType(postType)
                .resourceType(resourceType)
                .content(content)
                .build();
    }
}
