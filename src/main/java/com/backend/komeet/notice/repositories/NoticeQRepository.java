package com.backend.komeet.notice.repositories;

import com.backend.komeet.notice.model.dtos.NoticeDto;
import com.backend.komeet.user.model.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeQRepository {
    Page<NoticeDto> getNotices(
            User user,
            Pageable pageable
    );
}
