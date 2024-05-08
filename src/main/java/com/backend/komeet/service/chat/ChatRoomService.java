package com.backend.komeet.service.chat;

import com.backend.komeet.domain.ChatRoom;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.ChatRoomDto;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.exception.ErrorCode;
import com.backend.komeet.repository.ChatRoomRepository;
import com.backend.komeet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.backend.komeet.exception.ErrorCode.USER_INFO_NOT_FOUND;

/**
 * 채팅방 서비스
 */
@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    /**
     * 채팅방 목록을 조회하는 메서드
     *
     * @param userSeq 사용자 식별자
     * @param page    페이지
     * @return 채팅방 목록
     */
    @Transactional(readOnly = true)
    public Page<ChatRoomDto> getChatRooms(Long userSeq, Integer page) {
        Pageable pageable = PageRequest.of(page, 15);
        User user = userRepository
                .findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
        return chatRoomRepository.getChatRooms(user, pageable);
    }

    /**
     * 채팅방을 생성하는 메서드
     *
     * @param userSeq        사용자 식별자
     * @param counterpartSeq 상대방 식별자
     * @return 채팅방 식별자
     */
    @Transactional
    public Long createChatRoom(Long userSeq, Long counterpartSeq) {
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
     *
     * @param chatRoomSeq 채팅방 식별자
     * @param senderSeq   발신자 식별자
     * @return 채팅방 정보와 상대방 식별자
     */
    @Transactional
    public Pair<ChatRoomDto, Long> getChatRoomAndRecipient(Long chatRoomSeq,
                                                           Long senderSeq) {

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
     *
     * @param chatRoomSeq 채팅방 식별자
     * @param userSeq     사용자 식별자
     */
    @Transactional
    public void deleteChatRoom(Long chatRoomSeq, Long userSeq) {
        ChatRoom chatRoom = getChatRoom(chatRoomSeq);
        User user = getUser(userSeq);
        throwAnErrorIfUserIsNotInvolve(chatRoom, user);
        setChatRoomInvisible(chatRoom, user);
    }

    /**
     * 사용자가 채팅방에 참여하고 있는지 확인하는 메서드
     *
     * @param chatRoom {@link ChatRoom} 채팅방
     * @param user     {@link User} 사용자
     */
    private static void throwAnErrorIfUserIsNotInvolve(ChatRoom chatRoom,
                                                       User user) {
        if (!chatRoom.getSender().equals(user) && !chatRoom.getRecipient().equals(user)) {
            throw new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }
    }

    /**
     * 채팅방을 사용자에게 보이지 않도록 설정하는 메서드
     *
     * @param chatRoom {@link ChatRoom} 채팅방
     * @param user     {@link User} 사용자
     */
    private static void setChatRoomInvisible(ChatRoom chatRoom, User user) {
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
     *
     * @param chatRoomSeq 채팅방 식별자
     * @return {@link ChatRoom} 채팅방
     */
    private ChatRoom getChatRoom(Long chatRoomSeq) {
        return chatRoomRepository
                .findById(chatRoomSeq)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
    }

    /**
     * 사용자 식별자로 사용자 정보를 조회하는 메서드
     *
     * @param userSeq 사용자 식별자
     * @return 사용자 정보
     */
    private User getUser(Long userSeq) {
        return userRepository
                .findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }

    /**
     * 채팅방을 검색하는 메서드
     *
     * @param userSeq 사용자 식별자
     * @param keyword 검색어
     * @return 채팅방 목록
     */
    public List<ChatRoomDto> searchChatRooms(Long userSeq, String keyword) {
        User user = userRepository
                .findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
        return chatRoomRepository.searchChatRoomsByUserNickName(user, keyword);
    }
}
