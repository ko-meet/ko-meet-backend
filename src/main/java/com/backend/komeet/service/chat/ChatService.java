package com.backend.komeet.service.chat;

import com.backend.komeet.domain.Chat;
import com.backend.komeet.domain.ChatRoom;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.ChatDto;
import com.backend.komeet.dto.UserDto;
import com.backend.komeet.dto.request.ChatContentRequest;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.exception.ErrorCode;
import com.backend.komeet.repository.ChatRepository;
import com.backend.komeet.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @param userSeq 사용자 식별자
     * @param page 페이지
     * @return 채팅방 목록
     */
    @Transactional(readOnly = true)
    public Page<ChatDto> getChats(Long chatRoomSeq, Long userSeq, Integer page) {
        Pageable pageable = PageRequest.of(page, 15);
        return chatRepository.getChats(chatRoomSeq, userSeq, pageable);
    }

    @Transactional
    public void addChat(Long chatRoomSeq, Long userSeq, ChatContentRequest content) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomSeq)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        User sender = chatRoom.getSender();
        User recipient = chatRoom.getRecipient();
        isValidUser(sender, recipient, userSeq);
        boolean isSender = senderCheck(sender, userSeq);
        Chat chat = Chat.from(chatRoom,
                content.getContent(),
                isSender ? sender.getSeq() : recipient.getSeq(),
                isSender ? recipient.getSeq() : sender.getSeq(),
                false,
                content.getAttachments());
        chatRoom.getChats().add(chat);
    }

    private boolean senderCheck(User sender, Long userSeq) {
        return sender.getSeq().equals(userSeq);
    }

    private void isValidUser(User sender, User recipient, Long userSeq){
        if(!sender.getSeq().equals(userSeq) && !recipient.getSeq().equals(userSeq)) {
            throw new CustomException(ErrorCode.INVALID_USER);
        }
    }
}
