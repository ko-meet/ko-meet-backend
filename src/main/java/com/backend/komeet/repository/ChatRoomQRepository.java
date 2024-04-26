package com.backend.komeet.repository;

import com.backend.komeet.domain.User;
import com.backend.komeet.dto.ChatRoomDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 채팅방 관련 Querydsl 레포지토리 인터페이스
 */
public interface ChatRoomQRepository {

    Page<ChatRoomDto> getChatRooms(User user, Pageable pageable);

    Optional<ChatRoomDto> getChatRoom(User user1, User user2);

    List<ChatRoomDto> searchChatRoomsByUserNickName(User user, String keyword);

}
