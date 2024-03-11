package com.backend.komeet.dto;

import com.backend.komeet.domain.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 채팅방 관련 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatDto {
    private Long id;
    private Long chatRoomSeq;
    private String content;
    private UserDto sender;
    private UserDto recipient;
    private Boolean readStatus;
    private List<String> attachments;
    private LocalDateTime createdAt;

    /**
     * ChatDto 객체를 Chat 객체로 변환하는 메서드
     *
     * @param chat Chat 객체
     * @return ChatDto 객체
     */
    public static ChatDto from(Chat chat) {
        return ChatDto.builder()
                .id(chat.getSeq())
                .chatRoomSeq(chat.getChatRoom().getSeq())
                .content(chat.getContent())
                .sender(UserDto.from(chat.getSender()))
                .recipient(UserDto.from(chat.getRecipient()))
                .readStatus(chat.getReadStatus())
                .attachments(chat.getAttachments())
                .createdAt(chat.getCreatedAt())
                .build();
    }
}
