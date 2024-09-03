package com.backend.immilog.post.application.command;

import io.swagger.annotations.ApiModel;
import lombok.Builder;

import java.util.List;

@Builder
@ApiModel(value = "PostUpdateCommand", description = "게시물 수정 요청 Service DTO")
public record PostUpdateCommand(
        String title,
        String content,
        List<String> deleteTags,
        List<String> addTags,
        List<String> deleteAttachments,
        List<String> addAttachments,
        Boolean isPublic
) {
}


