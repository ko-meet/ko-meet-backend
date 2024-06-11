package com.backend.komeet.chat.repositories;

import com.backend.komeet.chat.model.dtos.ChatDto;
import com.backend.komeet.user.model.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 채팅 관련 Querydsl 레포지토리 인터페이스
 */
public interface ChatQRepository {
    Page<ChatDto> getChats(Long counterpartSeq, Long userSeq, Pageable pageable);
    boolean hasUnreadMessages(Long userSeq);
}
