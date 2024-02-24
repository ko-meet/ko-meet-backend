package com.backend.komeet.dto.request;

import lombok.*;
import java.util.List;

/**
 * 게시물 수정 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostUpdateRequest {

    private String title;
    private String content;
    private List<String> deleteTags;
    private List<String> addTags;
    private List<String> deleteAttachments;
    private List<String> addAttachments;
    private Boolean isPublic;

}
