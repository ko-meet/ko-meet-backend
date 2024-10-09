package com.backend.immilog.post.domain.vo;

import com.backend.immilog.post.domain.model.enums.Countries;
import com.backend.immilog.post.domain.model.enums.PostStatus;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

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

    public static <T extends Enum<T>> PostMetaData of(
            String title,
            String content,
            T country,
            String region
    ) {
        return PostMetaData.builder()
                .title(title)
                .content(content)
                .viewCount(0L)
                .likeCount(0L)
                .status(PostStatus.NORMAL)
                .country(Countries.valueOf(country.name()))
                .region(region)
                .build();
    }

}
