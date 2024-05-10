package com.backend.komeet.chat.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
