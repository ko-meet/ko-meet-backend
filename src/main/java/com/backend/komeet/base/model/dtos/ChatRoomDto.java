package com.backend.komeet.base.model.dtos;

import com.backend.komeet.user.model.dtos.UserDto;
import com.backend.komeet.chat.model.entities.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅방 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDto {
    private Long seq;
    private UserDto sender;
    private UserDto recipient;
    private String lastChat;
    private Integer unreadCountForSender;
    private Integer unreadCountForRecipient;
    private LocalDateTime lastChatTime;

    public static ChatRoomDto from(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .seq(chatRoom.getSeq())
                .sender(UserDto.from(chatRoom.getSender()))
                .recipient(UserDto.from(chatRoom.getRecipient()))
                .lastChat(
                        chatRoom.getChats().isEmpty() ?
                                " " :
                                chatRoom.getChats()
                                        .get(chatRoom.getChats().size() - 1)
                                        .getContent()
                )
                .lastChatTime(
                        chatRoom.getChats().isEmpty() ?
                                LocalDateTime.now() :
                                chatRoom.getChats()
                                        .get(chatRoom.getChats().size() - 1)
                                        .getCreatedAt()
                )
                .unreadCountForSender(
                        (int) chatRoom.getChats().stream()
                                .filter(chat -> chat.getSender()
                                        .getSeq()
                                        .equals(chatRoom.getRecipient().getSeq()))
                                .filter(chat -> !chat.getReadStatus())
                                .count()
                )
                .unreadCountForRecipient(
                        (int) chatRoom.getChats().stream()
                                .filter(chat -> chat.getSender()
                                        .getSeq()
                                        .equals(chatRoom.getSender().getSeq()))
                                .filter(chat -> !chat.getReadStatus())
                                .count()
                )
                .build();
    }
}
