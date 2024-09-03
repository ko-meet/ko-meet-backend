package com.backend.immilog.post.presentation.request;

import com.backend.immilog.post.application.command.PostUpdateCommand;
import io.swagger.annotations.ApiModel;
import lombok.Builder;

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
    public PostUpdateCommand toCommand() {
        return PostUpdateCommand.builder()
                .title(title)
                .content(content)
                .deleteTags(deleteTags)
                .addTags(addTags)
                .deleteAttachments(deleteAttachments)
                .addAttachments(addAttachments)
                .isPublic(isPublic)
                .build();
    }
}
