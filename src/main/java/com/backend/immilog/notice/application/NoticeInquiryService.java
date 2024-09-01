package com.backend.immilog.notice.application;

import com.backend.immilog.notice.model.dtos.NoticeDTO;
import com.backend.immilog.notice.model.repositories.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeInquiryService {
    private final NoticeRepository noticeRepository;

    public Page<NoticeDTO> getNotices(
            Long userSeq,
            Integer page
    ) {
        if(userSeq == null) {
            return Page.empty();
        }
        Pageable pageable = PageRequest.of(page, 10);
        return noticeRepository.getNotices(userSeq, pageable);
    }
}
