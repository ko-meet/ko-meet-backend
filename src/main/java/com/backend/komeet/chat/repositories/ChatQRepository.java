package com.backend.komeet.chat.repositories;

import com.backend.komeet.base.model.dtos.ChatDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 채팅 관련 Querydsl 레포지토리 인터페이스
 */
public interface ChatQRepository {
    Page<ChatDto> getChats(Long counterpartSeq, Long userSeq, Pageable pageable);
}
