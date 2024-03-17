package com.backend.komeet.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 댓글 업로드 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "CommentUploadRequest", description = "댓글 업로드 요청 DTO")
public class CommentUploadRequest {
    @NotNull(message = "댓글 내용을 입력해주세요.")
    private String content;
}
