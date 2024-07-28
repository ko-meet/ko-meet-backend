package com.backend.komeet.notice.application;

import com.backend.komeet.global.exception.CustomException;
import com.backend.komeet.notice.model.entities.Notice;
import com.backend.komeet.notice.presentation.request.NoticeRegisterRequest;
import com.backend.komeet.notice.repositories.NoticeRepository;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.global.exception.ErrorCode.NOT_AN_ADMIN_USER;
import static com.backend.komeet.global.exception.ErrorCode.USER_INFO_NOT_FOUND;
import static com.backend.komeet.user.enums.UserRole.ROLE_ADMIN;

@Service
@RequiredArgsConstructor
public class NoticeRegisterService {
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 등록
     */
    @Transactional
    public void registerNotice(
            Long userSeq,
            NoticeRegisterRequest request
    ) {
        if (!isUserAdmin(userSeq)) {
            throw new CustomException(NOT_AN_ADMIN_USER);
        }
        noticeRepository.save(Notice.from(userSeq, request));
    }

    /**
     * 사용자가 관리자가 아닌지 확인
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
}
