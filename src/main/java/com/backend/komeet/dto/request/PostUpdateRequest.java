package com.backend.komeet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
    private List<String> deleteTags = new ArrayList<>();
    private List<String> addTags = new ArrayList<>();
    private List<String> deleteAttachments = new ArrayList<>();
    private List<String> addAttachments = new ArrayList<>();
    private Boolean isPublic;

}
