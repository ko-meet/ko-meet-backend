package com.backend.immilog.post.model.entities;

import com.backend.immilog.global.model.BaseDateEntity;
import com.backend.immilog.post.enums.Categories;
import com.backend.immilog.post.model.embeddables.PostMetaData;
import com.backend.immilog.post.presentation.request.PostUploadRequest;
import com.backend.immilog.user.enums.Countries;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@DynamicUpdate
@Entity
public class Post extends BaseDateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private Long userSeq;

    @Embedded
    private PostMetaData postMetaData;

    @Enumerated(EnumType.STRING)
    private Categories category;

    @Setter
    private String isPublic;

    @Setter
    private Long commentCount;

    public static Post of(
            PostUploadRequest postUploadRequest,
            Long userSeq,
            Countries country,
            String region
    ) {
        PostMetaData postMetaData = PostMetaData.of(
                postUploadRequest,
                country,
                region
        );
        return Post.builder()
                .userSeq(userSeq)
                .postMetaData(postMetaData)
                .category(postUploadRequest.getCategory())
                .isPublic(postUploadRequest.getIsPublic() ? "Y" : "N")
                .commentCount(0L)
                .build();
    }
}
