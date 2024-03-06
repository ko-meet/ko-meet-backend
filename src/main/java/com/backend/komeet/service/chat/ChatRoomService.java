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

import java.util.Objects;

import static com.backend.komeet.exception.ErrorCode.USER_INFO_NOT_FOUND;

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
        User user = userRepository.findById(userSeq).orElseThrow(
                () -> new CustomException(USER_INFO_NOT_FOUND)
        );
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
        return chatRoomRepository.getChatRoom(user, counterpart)
                .map(ChatRoomDto::getSeq)
                .orElseGet(() -> chatRoomRepository.save(ChatRoom.from(user, counterpart)).getSeq());
    }

    /**
     * 채팅방과 상대방 식별자를 조회하는 메서드
     * @param chatRoomSeq 채팅방 식별자
     * @param senderSeq  발신자 식별자
     * @return 채팅방 정보와 상대방 식별자
     */
    @Transactional
    public Pair<ChatRoomDto,Long> getChatRoomAndRecipient(Long chatRoomSeq,
                                                          Long senderSeq) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomSeq).orElseThrow(
                () -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND)
        );

        ChatRoomDto chatRoomDto = ChatRoomDto.from(chatRoom);

        Long recipientSeq =
                Objects.equals(senderSeq, chatRoom.getRecipient().getSeq()) ?
                        chatRoom.getSender().getSeq() :
                        chatRoom.getRecipient().getSeq();

        return Pair.of(chatRoomDto, recipientSeq);
    }

    /**
     * 사용자 식별자로 사용자 정보를 조회하는 메서드
     *
     * @param userSeq 사용자 식별자
     * @return 사용자 정보
     */
    private User getUser(Long userSeq) {
        return userRepository.findById(userSeq).orElseThrow(
                () -> new CustomException(USER_INFO_NOT_FOUND)
        );
    }
}
