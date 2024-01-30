package com.backend.komeet.dto.request;

import com.backend.komeet.enums.Categories;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 게시물 생성 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
