package com.backend.komeet.post.presentation.request;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

/**
 * 게시물 수정 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "PostUpdateRequest", description = "게시물 수정 요청 DTO")
public class PostUpdateRequest {

    private String title;
    private String content;
    private List<String> deleteTags;
    private List<String> addTags;
    private List<String> deleteAttachments;
    private List<String> addAttachments;
    private Boolean isPublic;

}
