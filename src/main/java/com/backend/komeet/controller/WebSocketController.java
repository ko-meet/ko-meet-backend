package com.backend.komeet.controller;

import com.backend.komeet.domain.Chat;
import com.backend.komeet.dto.ChatDto;
import com.backend.komeet.dto.ReadChatDto;
import com.backend.komeet.dto.request.ChatContentRequest;
import com.backend.komeet.dto.request.ChatRequest;
import com.backend.komeet.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class WebSocketController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send")
    public ChatDto sendMessage(@Payload ChatRequest chatRequest) {
        Chat chat = chatService.saveChat(chatRequest);// 메시지 저장
        ChatDto chatDto = ChatDto.from(chat);
        messagingTemplate.convertAndSend(
                "/topic/room/" + chatRequest.getChatRoomSeq(), chatDto
        );
        return chatDto;
    }

    @MessageMapping("/chat/read")
    public ReadChatDto markMessageAsRead(@Payload ReadChatDto readChatDto) {
        chatService.markAsRead(readChatDto.getChatSeq(), readChatDto.getUserSeq());
        return readChatDto;
    }
}
