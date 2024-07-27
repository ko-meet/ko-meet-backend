package com.backend.komeet.chat.application;

import com.backend.komeet.chat.model.entities.Chat;
import com.backend.komeet.chat.model.entities.ChatRoom;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.chat.model.dtos.ChatDto;
import com.backend.komeet.chat.presentation.request.ChatContentRequest;
import com.backend.komeet.chat.presentation.request.ChatRequest;
import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.chat.repositories.ChatRepository;
import com.backend.komeet.chat.repositories.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.infrastructure.exception.ErrorCode.*;

/**
 * 채팅방 관련 서비스
 */
@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 채팅방 목록을 조회하는 메서드
     */
    @Transactional(readOnly = true)
    public Page<ChatDto> getChats(
            Long chatRoomSeq,
            Long userSeq,
            Integer page
    ) {
        Pageable pageable = PageRequest.of(page, 50);
        return chatRepository.getChats(chatRoomSeq, userSeq, pageable);
    }

    /**
     * 채팅방에 채팅을 추가하는 메서드
     */
    @Transactional
    public void addChat(
            Long chatRoomSeq,
            Long userSeq,
            ChatContentRequest content
    ) {
        ChatRoom chatRoom = chatRoomRepository
                .findById(chatRoomSeq)
                .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));
        User sender = chatRoom.getSender();
        User recipient = chatRoom.getRecipient();
        isValidUser(sender, recipient, userSeq);
        boolean isSender = senderCheck(sender, userSeq);
        Chat chat = Chat.from(chatRoom,
                content.getContent(),
                isSender ? sender : recipient,
                isSender ? recipient : sender,
                false,
                content.getAttachments());
        chatRoom.getChats().add(chat);
    }

    /**
     * 채팅을 저장하는 메서드
     */
    @Transactional
    public Chat saveChat(
            ChatRequest chatRequest
    ) {
        ChatRoom chatRoom = chatRoomRepository
                .findById(chatRequest.getChatRoomSeq())
                .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        boolean isSender = senderCheck(chatRoom.getSender(), chatRequest.getSenderSeq());

        Chat chat = Chat.from(chatRoom,
                chatRequest.getContent(),
                isSender ? chatRoom.getSender() : chatRoom.getRecipient(),
                isSender ? chatRoom.getRecipient() : chatRoom.getSender(),
                false,
                chatRequest.getAttachments());

        chatRoom.getChats().add(chat);
        setChatRoomVisible(isSender, chatRoom);

        return chat;
    }

    /**
     * 채팅을 읽음으로 표시하는 메서드
     */
    @Transactional
    public void markAsRead(
            Long chatSeq,
            Long userSeq
    ) {
        Chat chat = chatRepository
                .findById(chatSeq)
                .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        if (chat.getRecipient().getSeq().equals(userSeq)) {
            chat.setReadStatus(true);
        }
    }

    /**
     * 채팅방 송신자 확인 메서드
     */
    private boolean senderCheck(
            User sender,
            Long userSeq
    ) {
        return sender.getSeq().equals(userSeq);
    }

    /**
     * 사용자가 채팅방에 속해있는지 확인하는 메서드
     */
    private void isValidUser(
            User sender,
            User recipient,
            Long userSeq
    ) {
        if (!sender.getSeq().equals(userSeq) && !recipient.getSeq().equals(userSeq)) {
            throw new CustomException(INVALID_USER);
        }
    }

    /**
     * 채팅방을 보이도록 설정하는 메서드
     */
    private void setChatRoomVisible(
            boolean isSender,
            ChatRoom chatRoom
    ) {
        if (isSender) {
            chatRoom.setIsVisibleToRecipient(true);
        } else {
            chatRoom.setIsVisibleToSender(true);
        }
    }

}
