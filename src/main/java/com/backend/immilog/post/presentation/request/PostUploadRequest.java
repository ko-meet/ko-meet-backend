package com.backend.immilog.post.presentation.request;

import com.backend.immilog.post.application.command.PostUploadCommand;
import com.backend.immilog.post.domain.enums.Categories;
import io.swagger.annotations.ApiModel;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@ApiModel(value = "PostUploadRequest", description = "게시물 생성 요청 DTO")
public record PostUploadRequest(
        @NotBlank(message = "제목을 입력해주세요.") String title,
        @NotBlank(message = "내용을 입력해주세요.") String content,
        List<String> tags,
        List<String> attachments,
        @NotNull(message = "전체공개 여부를 입력해주세요.") Boolean isPublic,
        @NotNull(message = "카테고리를 입력해주세요.") Categories category
) {
    public PostUploadCommand toCommand() {
        return PostUploadCommand.builder()
                .title(title)
                .content(content)
                .tags(tags)
                .attachments(attachments)
                .isPublic(isPublic)
                .category(category)
                .build();
    }
}
