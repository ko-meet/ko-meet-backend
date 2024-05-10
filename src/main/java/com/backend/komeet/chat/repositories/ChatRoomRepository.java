package com.backend.komeet.chat.repositories;

import com.backend.komeet.chat.model.entities.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 채팅방 레포지토리
 */
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> , ChatRoomQRepository {
}
