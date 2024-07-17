package com.backend.komeet.chat.presentation.request;

import io.swagger.annotations.ApiModel;
import lombok.*;

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
