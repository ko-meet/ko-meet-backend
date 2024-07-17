package com.backend.komeet.chat.model.dtos;

import lombok.*;

/**
 * 채팅 읽음 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReadChatDto {
    private Long chatSeq;
    private Long userSeq;
}
