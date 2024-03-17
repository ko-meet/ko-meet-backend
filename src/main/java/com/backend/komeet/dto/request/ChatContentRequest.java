package com.backend.komeet.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 채팅 내용 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "ChatContentRequest", description = "채팅 내용 요청 DTO")
public class ChatContentRequest {
    private String content;
    private List<String> attachments;
}
