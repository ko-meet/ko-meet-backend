package com.backend.komeet.notice.application;

import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.notice.model.dtos.NoticeDto;
import com.backend.komeet.notice.repositories.NoticeRepository;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.infrastructure.exception.ErrorCode.USER_INFO_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class NoticeInquiryService {
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    /**
     * 공지사항 조회
     *
     * @param userSeq 사용자 고유번호
     * @param page    페이지 번호
     * @return {@link Page<NoticeDto>}
     */
    @Transactional(readOnly = true)
    public Page<NoticeDto> getNotices(Long userSeq, Integer page) {
        User user = getUser(userSeq);
        Pageable pageable = PageRequest.of(page, 10);
        return noticeRepository.getNotices(user, pageable);
    }

    /**
     * 사용자 정보 조회
     *
     * @param userSeq 사용자 고유번호
     * @return {@link User}
     */
    private User getUser(Long userSeq) {
        return userRepository
                .findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }
}
