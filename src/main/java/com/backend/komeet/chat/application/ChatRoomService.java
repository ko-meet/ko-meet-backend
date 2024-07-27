package com.backend.komeet.chat.application;

import com.backend.komeet.chat.model.dtos.ChatRoomDto;
import com.backend.komeet.chat.model.entities.ChatRoom;
import com.backend.komeet.chat.repositories.ChatRepository;
import com.backend.komeet.chat.repositories.ChatRoomRepository;
import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.infrastructure.exception.ErrorCode;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.backend.komeet.infrastructure.exception.ErrorCode.USER_INFO_NOT_FOUND;

/**
 * 채팅방 서비스
 */
@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    /**
     * 채팅방 목록을 조회하는 메서드
     */
    @Transactional(readOnly = true)
    public Page<ChatRoomDto> getChatRooms(
            Long userSeq,
            Integer page
    ) {
        Pageable pageable = PageRequest.of(page, 15);
        User user = userRepository
                .findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
        return chatRoomRepository.getChatRooms(user, pageable);
    }

    /**
     * 채팅방을 생성하는 메서드
     */
    @Transactional
    public Long createChatRoom(
            Long userSeq,
            Long counterpartSeq
    ) {
        User user = getUser(userSeq);
        User counterpart = getUser(counterpartSeq);
        return chatRoomRepository
                .getChatRoom(user, counterpart)
                .map(ChatRoomDto::getSeq)
                .orElseGet(
                        () -> chatRoomRepository
                                .save(ChatRoom.from(user, counterpart))
                                .getSeq()
                );
    }

    /**
     * 채팅방과 상대방 식별자를 조회하는 메서드
     */
    @Transactional
    public Pair<ChatRoomDto, Long> getChatRoomAndRecipient(
            Long chatRoomSeq,
            Long senderSeq
    ) {

        ChatRoom chatRoom = getChatRoom(chatRoomSeq);

        ChatRoomDto chatRoomDto = ChatRoomDto.from(chatRoom);

        Long recipientSeq =
                Objects.equals(senderSeq, chatRoom.getRecipient().getSeq()) ?
                        chatRoom.getSender().getSeq() :
                        chatRoom.getRecipient().getSeq();

        return Pair.of(chatRoomDto, recipientSeq);
    }

    /**
     * 채팅방을 삭제하는 메서드
     */
    @Transactional
    public void deleteChatRoom(
            Long chatRoomSeq,
            Long userSeq
    ) {
        ChatRoom chatRoom = getChatRoom(chatRoomSeq);
        User user = getUser(userSeq);
        throwAnErrorIfUserIsNotInvolve(chatRoom, user);
        setChatRoomInvisible(chatRoom, user);
    }

    /**
     * 사용자가 채팅방에 참여하고 있는지 확인하는 메서드
     */
    private static void throwAnErrorIfUserIsNotInvolve(
            ChatRoom chatRoom,
            User user
    ) {
        if (!chatRoom.getSender().equals(user) && !chatRoom.getRecipient().equals(user)) {
            throw new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }
    }

    /**
     * 채팅방을 사용자에게 보이지 않도록 설정하는 메서드
     */
    private static void setChatRoomInvisible(
            ChatRoom chatRoom,
            User user
    ) {
        boolean isSender = chatRoom.getSender().equals(user);
        if (isSender) {
            chatRoom.setIsVisibleToSender(false);
            chatRoom.getChats().forEach(chat -> {
                chat.setInvisibleToSender(user.getSeq());
            });
        } else {
            chatRoom.setIsVisibleToRecipient(false);
            chatRoom.getChats().forEach(chat -> {
                chat.setInvisibleToRecipient(user.getSeq());
            });
        }

    }

    /**
     * 채팅방을 조회하는 메서드
     */
    private ChatRoom getChatRoom(
            Long chatRoomSeq
    ) {
        return chatRoomRepository
                .findById(chatRoomSeq)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
    }

    /**
     * 사용자 식별자로 사용자 정보를 조회하는 메서드
     */
    private User getUser(
            Long userSeq
    ) {
        return userRepository
                .findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }

    /**
     * 채팅방을 검색하는 메서드
     */
    public List<ChatRoomDto> searchChatRooms(
            Long userSeq,
            String keyword
    ) {
        User user = userRepository
                .findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
        return chatRoomRepository.searchChatRoomsByUserNickName(user, keyword);
    }

    /**
     * 사용자가 읽지 않은 채팅이 있는지 확인하는 메서드
     */
    public boolean getNewChatRooms(
            Long userSeq
    ) {
        return chatRepository.hasUnreadMessages(userSeq);
    }
}
