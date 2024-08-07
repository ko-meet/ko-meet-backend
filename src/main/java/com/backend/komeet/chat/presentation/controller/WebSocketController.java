package com.backend.komeet.chat.presentation.controller;

import com.backend.komeet.chat.model.entities.Chat;
import com.backend.komeet.chat.model.dtos.ChatDto;
import com.backend.komeet.chat.model.dtos.ChatRoomDto;
import com.backend.komeet.chat.model.dtos.ReadChatDto;
import com.backend.komeet.chat.application.ChatRoomService;
import com.backend.komeet.chat.application.ChatService;
import com.backend.komeet.chat.presentation.request.ChatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket 컨트롤러
 */
@RequiredArgsConstructor
@Controller
public class WebSocketController {
    private final ChatService chatService;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 메시지 전송
     */
    @MessageMapping("/chat/send")
    public ChatDto sendMessage(
            @Payload ChatRequest chatRequest
    ) {
        Chat chat = chatService.saveChat(chatRequest);// 메시지 저장
        ChatDto chatDto = ChatDto.from(chat);
        messagingTemplate.convertAndSend(
                "/topic/room/" + chatRequest.getChatRoomSeq(), chatDto
        );
        Pair<ChatRoomDto, Long> result = chatRoomService.getChatRoomAndRecipient(
                chatRequest.getChatRoomSeq(), chatRequest.getSenderSeq()
        );

        messagingTemplate.convertAndSend(
                "/topic/updateChatRoomList/" + result.getSecond(),
                result.getFirst()
        );
        return chatDto;
    }

    /**
     * 메시지 읽음 처리
     */
    @MessageMapping("/chat/read")
    public ReadChatDto markMessageAsRead(
            @Payload ReadChatDto readChatDto
    ) {
        chatService.markAsRead(readChatDto.getChatSeq(), readChatDto.getUserSeq());
        messagingTemplate.convertAndSend("/topic/readChat", readChatDto);
        return readChatDto;
    }
}
