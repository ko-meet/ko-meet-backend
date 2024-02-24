package com.backend.komeet.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentUploadRequest {
    @NotNull(message = "댓글 내용을 입력해주세요.")
    private String content;
}
