package com.backend.komeet.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
