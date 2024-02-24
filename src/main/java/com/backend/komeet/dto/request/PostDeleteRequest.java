package com.backend.komeet.dto.request;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDeleteRequest {
    private Long postSeq;
}
