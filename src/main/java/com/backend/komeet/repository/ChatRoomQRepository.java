package com.backend.komeet.repository;

import com.backend.komeet.domain.User;
import com.backend.komeet.dto.ChatDto;
import com.backend.komeet.dto.ChatRoomDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatRoomQRepository {

    Page<ChatRoomDto> getChatRooms(User user, Pageable pageable);
}
