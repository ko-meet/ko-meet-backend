package com.backend.komeet.notice.application;

import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.notice.model.dtos.NoticeDto;
import com.backend.komeet.notice.model.entities.Notice;
import com.backend.komeet.notice.repositories.NoticeRepository;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.infrastructure.exception.ErrorCode.*;
import static com.backend.komeet.post.enums.PostStatus.DELETED;

@Service
@RequiredArgsConstructor
public class NoticeInquiryService {
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    /**
     * 공지사항 조회
     */
    @Transactional(readOnly = true)
    public Page<NoticeDto> getNotices(
            Long userSeq,
            Integer page
    ) {
        User user = getUser(userSeq);
        Pageable pageable = PageRequest.of(page, 10);
        return noticeRepository.getNotices(user, pageable);
    }

    /**
     * 공지사항 상세조회
     */
    @Transactional
    public NoticeDto getNotice(
            Long userSeq,
            Long noticeSeq
    ) {
        Notice notice = getNoticeBySeq(noticeSeq);
        validateCountry(userSeq, notice);
        addReadUserIfItsUnreadUser(userSeq, notice);
        if (notice.getStatus().equals(DELETED)) {
            throw new CustomException(NOTICE_ALREADY_DELETED);
        }
        return NoticeDto.from(notice);
    }

    /**
     * 공지사항 존재 여부 확인
     */
    @Transactional(readOnly = true)
    public Boolean isNoticeExist(
            Long userSeq
    ) {
        User user = getUser(userSeq);
        return noticeRepository.existsByTargetCountriesContainingAndReadUsersNotContaining(
                user.getCountry(), user.getSeq()
        );
    }

    /**
     * 공지사항 조회시 읽은 사용자 추가
     */
    private static void addReadUserIfItsUnreadUser(
            Long userSeq,
            Notice notice
    ) {
        if (!notice.getReadUsers().contains(userSeq)) {
            notice.getReadUsers().add(userSeq);
        }
    }

    /**
     * 사용자 국가 검증
     */
    private void validateCountry(
            Long userSeq,
            Notice notice
    ) {
        if (!notice.getTargetCountries().contains(getUser(userSeq).getCountry())) {
            throw new CustomException(NOT_ELIGIBLE_COUNTRY);
        }
    }

    /**
     * 공지사항 조회
     */
    private Notice getNoticeBySeq(
            Long noticeSeq
    ) {
        return noticeRepository
                .findById(noticeSeq)
                .orElseThrow(() -> new CustomException(NOTICE_NOT_FOUND));
    }

    /**
     * 사용자 정보 조회
     */
    private User getUser(
            Long userSeq
    ) {
        return userRepository
                .findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }
}
