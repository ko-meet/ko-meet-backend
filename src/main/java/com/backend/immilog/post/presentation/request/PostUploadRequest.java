package com.backend.immilog.post.presentation.request;

import com.backend.immilog.post.enums.Categories;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "PostUploadRequest", description = "게시물 생성 요청 DTO")
public class PostUploadRequest {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    private List<String> tags;

    private List<String> attachments;

    @NotNull(message = "전체공개 여부를 입력해주세요.")
    private Boolean isPublic;

    private Categories category;
}
