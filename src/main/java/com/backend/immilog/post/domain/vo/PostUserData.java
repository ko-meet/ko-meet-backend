package com.backend.immilog.post.domain.vo;

import lombok.*;

import jakarta.persistence.Embeddable;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Embeddable
public class PostUserData {
    private Long userSeq;
    private String nickname;
    private String profileImage;
}
