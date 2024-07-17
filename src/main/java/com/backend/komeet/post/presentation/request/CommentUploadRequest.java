package com.backend.komeet.post.presentation.request;

import io.swagger.annotations.ApiModel;
import lombok.*;

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
