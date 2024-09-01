package com.backend.immilog.notice.model.repositories;

import com.backend.immilog.notice.model.dtos.NoticeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {
    Page<NoticeDTO> getNotices(
            Long userSeq,
            Pageable pageable
    );
}
