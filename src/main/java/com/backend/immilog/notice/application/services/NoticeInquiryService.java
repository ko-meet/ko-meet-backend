package com.backend.immilog.notice.application.services;

import com.backend.immilog.notice.exception.NoticeException;
import com.backend.immilog.notice.application.dtos.NoticeDTO;
import com.backend.immilog.notice.model.repositories.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.immilog.notice.exception.NoticeErrorCode.NOTICE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class NoticeInquiryService {
    private final NoticeRepository noticeRepository;

    @Transactional
    public Page<NoticeDTO> getNotices(
            Long userSeq,
            Integer page
    ) {
        if (userSeq == null) {
            return Page.empty();
        }
        Pageable pageable = PageRequest.of(page, 10);
        return noticeRepository.getNotices(userSeq, pageable);
    }

    @Transactional
    public NoticeDTO getNoticeDetail(
            Long noticeSeq
    ) {
        return noticeRepository
                .findById(noticeSeq)
                .map(NoticeDTO::from)
                .orElseThrow(() -> new NoticeException(NOTICE_NOT_FOUND));
    }
}
