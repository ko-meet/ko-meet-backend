package com.backend.komeet.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 채팅 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "ChatRequest", description = "채팅 요청 DTO")
public class ChatRequest {
    private Long chatRoomSeq;
    private String content;
    private Long senderSeq;
    private List<String> attachments;
}
