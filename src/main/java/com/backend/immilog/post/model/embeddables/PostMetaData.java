package com.backend.immilog.post.model.embeddables;


import com.backend.immilog.post.enums.PostStatus;
import com.backend.immilog.post.presentation.request.PostUploadRequest;
import com.backend.immilog.user.enums.Countries;
import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Embeddable
public class PostMetaData {
    @Setter
    private String title;

    @Setter
    private String content;

    @Setter
    private Long viewCount;

    @Setter
    private Long likeCount;

    private String region;

    @Setter
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @Enumerated(EnumType.STRING)
    private Countries country;

    public static PostMetaData of(
            PostUploadRequest postUploadRequest,
            Countries country,
            String region
    ) {
        return PostMetaData.builder()
                .title(postUploadRequest.title())
                .content(postUploadRequest.content())
                .viewCount(0L)
                .likeCount(0L)
                .status(PostStatus.NORMAL)
                .country(country)
                .region(region)
                .build();
    }
}
