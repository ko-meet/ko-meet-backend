package com.backend.immilog.post.domain.vo;

import com.backend.immilog.post.application.command.PostUploadCommand;
import com.backend.immilog.post.domain.model.enums.Countries;
import com.backend.immilog.post.domain.model.enums.PostStatus;
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
            PostUploadCommand postUploadCommand,
            Countries country,
            String region
    ) {
        return PostMetaData.builder()
                .title(postUploadCommand.title())
                .content(postUploadCommand.content())
                .viewCount(0L)
                .likeCount(0L)
                .status(PostStatus.NORMAL)
                .country(country)
                .region(region)
                .build();
    }
}
