package com.backend.immilog.post.presentation.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@ApiModel(value = "PostUpdateRequest", description = "게시물 수정 요청 DTO")
public record PostUpdateRequest(
        String title,
        String content,
        List<String> deleteTags,
        List<String> addTags,
        List<String> deleteAttachments,
        List<String> addAttachments,
        Boolean isPublic
) {
}
