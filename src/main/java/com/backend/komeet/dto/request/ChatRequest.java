package com.backend.komeet.dto.request;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRequest {
    private Long chatRoomSeq;
    private String content;
    private Long senderSeq;
    private List<String> attachments;
}
