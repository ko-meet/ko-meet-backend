package com.backend.komeet.post.presentation.request;

import io.swagger.annotations.ApiModel;
import lombok.*;

/**
 * 게시물 삭제 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "PostDeleteRequest", description = "게시물 삭제 요청 DTO")
public class PostDeleteRequest {
    private Long postSeq;
}
