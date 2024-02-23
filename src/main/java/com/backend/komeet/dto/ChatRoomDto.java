package com.backend.komeet.dto;

import com.backend.komeet.domain.Chat;
import com.backend.komeet.domain.ChatRoom;
import com.backend.komeet.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDto {
    private Long seq;
    private UserDto sender;
    private UserDto recipient;
    private String lastChat;
    private LocalDateTime lastChatTime;

    public static ChatRoomDto from(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .seq(chatRoom.getSeq())
                .sender(UserDto.from(chatRoom.getSender()))
                .recipient(UserDto.from(chatRoom.getRecipient()))
                .lastChat(chatRoom.getChats().get(chatRoom.getChats().size() - 1).getContent())
                .lastChatTime(chatRoom.getChats().get(chatRoom.getChats().size() - 1).getCreatedAt())
                .build();
    }
}
