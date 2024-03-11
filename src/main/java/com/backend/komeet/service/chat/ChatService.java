package com.backend.komeet.service.chat;

import com.backend.komeet.domain.Chat;
import com.backend.komeet.domain.ChatRoom;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.ChatDto;
import com.backend.komeet.dto.request.ChatContentRequest;
import com.backend.komeet.dto.request.ChatRequest;
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
     *
     * @param userSeq 사용자 식별자
     * @param page    페이지
     * @return 채팅방 목록
     */
    @Transactional(readOnly = true)
    public Page<ChatDto> getChats(Long chatRoomSeq, Long userSeq, Integer page) {
        Pageable pageable = PageRequest.of(page, 50);
        return chatRepository.getChats(chatRoomSeq, userSeq, pageable);
    }

    /**
     * 채팅방에 채팅을 추가하는 메서드
     *
     * @param chatRoomSeq 채팅방 식별자
     * @param userSeq     사용자 식별자
     * @param content     채팅 내용
     */
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
                isSender ? sender : recipient,
                isSender ? recipient : sender,
                false,
                content.getAttachments());
        chatRoom.getChats().add(chat);
    }

    /**
     * 채팅을 저장하는 메서드
     *
     * @param chatRequest 채팅 메세지 요청
     */
    @Transactional
    public Chat saveChat(ChatRequest chatRequest) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRequest.getChatRoomSeq())
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        boolean isSender = senderCheck(chatRoom.getSender(), chatRequest.getSenderSeq());

        Chat chat = Chat.from(chatRoom,
                chatRequest.getContent(),
                isSender ? chatRoom.getSender() : chatRoom.getRecipient(),
                isSender ? chatRoom.getRecipient() : chatRoom.getSender(),
                false,
                chatRequest.getAttachments());

        chatRoom.getChats().add(chat);

        return chat;
    }

    /**
     * 채팅을 읽음으로 표시하는 메서드
     *
     * @param chatSeq 채팅 식별자
     * @param userSeq 사용자 식별자
     */
    @Transactional
    public void markAsRead(Long chatSeq, Long userSeq) {

        Chat chat = chatRepository.findById(chatSeq).orElseThrow(
                () -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND)
        );

        if (chat.getRecipient().getSeq().equals(userSeq)) {
            chat.setReadStatus(true);
        }
    }

    /**
     * 채팅방 송신자 확인 메서드
     *
     * @param sender  채팅방 송신자
     * @param userSeq 사용자 식별자
     * @return 채팅방 송신자 확인 결과
     */
    private boolean senderCheck(User sender, Long userSeq) {
        return sender.getSeq().equals(userSeq);
    }

    /**
     * 사용자가 채팅방에 속해있는지 확인하는 메서드
     *
     * @param sender    채팅방 송신자
     * @param recipient 채팅방 수신자
     * @param userSeq   사용자 식별자
     */
    private void isValidUser(User sender, User recipient, Long userSeq) {
        if (!sender.getSeq().equals(userSeq) && !recipient.getSeq().equals(userSeq)) {
            throw new CustomException(ErrorCode.INVALID_USER);
        }
    }
}
