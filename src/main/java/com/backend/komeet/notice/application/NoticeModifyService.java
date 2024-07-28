package com.backend.komeet.notice.application;

import com.backend.komeet.global.exception.CustomException;
import com.backend.komeet.notice.model.entities.Notice;
import com.backend.komeet.notice.presentation.request.NoticeModifyRequest;
import com.backend.komeet.notice.repositories.NoticeRepository;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.global.exception.ErrorCode.*;
import static com.backend.komeet.post.enums.PostStatus.DELETED;
import static com.backend.komeet.user.enums.UserRole.ROLE_ADMIN;

@Service
@RequiredArgsConstructor
public class NoticeModifyService {
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 수정
     */
    @Transactional
    public void modifyNotice(
            Long userSeq,
            Long noticeSeq,
            NoticeModifyRequest request
    ) {

        if (!isUserAdmin(userSeq)) {
            throw new CustomException(NOT_AN_ADMIN_USER);
        }
        Notice notice = getNoticeBySeq(noticeSeq);

        if (notice.getStatus().equals(DELETED)) {
            throw new CustomException(NOTICE_ALREADY_DELETED);
        }

        setIfItsNotNull(request, notice);
    }

    /**
     * 수정할 내용이 null이 아닌 경우 수정
     */
    private void setIfItsNotNull(
            NoticeModifyRequest request,
            Notice notice
    ) {

        if (request.getContent() != null) {
            notice.setContent(request.getContent());
        }
        if (request.getTitle() != null) {
            notice.setTitle(request.getTitle());
        }
        if (request.getType() != null) {
            notice.setType(request.getType());
        }
        if (request.getStatus() != null) {
            notice.setStatus(request.getStatus());
        }
    }

    /**
     * 사용자가 관리자인지 확인
     */
    private boolean isUserAdmin(
            Long userSeq
    ) {
        return getUser(userSeq)
                .getUserRole()
                .equals(ROLE_ADMIN);
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
}
