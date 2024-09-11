package com.backend.immilog.post.infrastructure.jpa.entities;

import com.backend.immilog.global.model.BaseDateEntity;
import com.backend.immilog.post.domain.vo.PostMetaData;
import com.backend.immilog.post.domain.vo.PostUserData;
import com.backend.immilog.post.domain.model.Post;
import com.backend.immilog.post.domain.model.enums.Categories;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@DynamicUpdate
@Entity
@Table(name = "post")
public class PostEntity extends BaseDateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Embedded
    private PostUserData postUserData;

    @Embedded
    private PostMetaData postMetaData;

    @Enumerated(EnumType.STRING)
    private Categories category;

    @Setter
    private String isPublic;

    @Setter
    private Long commentCount;

    public static PostEntity from(
            Post post
    ){
        return PostEntity.builder()
                .seq(post.seq())
                .postUserData(post.postUserData())
                .postMetaData(post.postMetaData())
                .category(post.category())
                .isPublic(post.isPublic())
                .commentCount(post.commentCount())
                .build();
    }

    public Post toDomain() {
        return Post.builder()
                .seq(this.seq)
                .postUserData(this.postUserData)
                .postMetaData(this.postMetaData)
                .category(this.category)
                .isPublic(this.isPublic)
                .commentCount(this.commentCount)
                .build();
    }
}
