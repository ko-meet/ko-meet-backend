package com.backend.komeet.service.chat;

import com.backend.komeet.dto.ChatDto;
import com.backend.komeet.dto.ChatRoomDto;
import com.backend.komeet.repository.ChatRepository;
import com.backend.komeet.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 채팅방 관련 서비스
 */
@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatRepository chatRepository;

    /**
     * 채팅방 목록을 조회하는 메서드
     * @param userSeq 사용자 식별자
     * @param page 페이지
     * @return 채팅방 목록
     */
    public Page<ChatDto> getChats(Long counterpartSeq, Long userSeq, Integer page) {
        Pageable pageable = PageRequest.of(page, 15);
        return chatRepository.getChats(counterpartSeq, userSeq, pageable);
    }
}
