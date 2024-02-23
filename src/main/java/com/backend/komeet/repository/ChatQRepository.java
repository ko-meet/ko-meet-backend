package com.backend.komeet.repository;

import com.backend.komeet.dto.ChatDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatQRepository {
    Page<ChatDto> getChats(Long charRoomSeq, Long userSeq, Pageable pageable);
}
