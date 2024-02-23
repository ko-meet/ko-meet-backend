package com.backend.komeet.service.chat;

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
import org.springframework.stereotype.Service;

import static com.backend.komeet.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    /**
     * 채팅방 목록을 조회하는 메서드
     * @param userSeq 사용자 식별자
     * @param page 페이지
     * @return 채팅방 목록
     */
    public Page<ChatRoomDto> getChatRooms(Long userSeq, Integer page) {
        Pageable pageable = PageRequest.of(page, 15);
        User user = userRepository.findById(userSeq).orElseThrow(
                ()-> new CustomException(USER_INFO_NOT_FOUND)
        );
        return chatRoomRepository.getChatRooms(user, pageable);
    }
}
