package com.backend.komeet.repository;

import com.backend.komeet.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 채팅방 레포지토리
 */
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> , ChatRoomQRepository {
}
