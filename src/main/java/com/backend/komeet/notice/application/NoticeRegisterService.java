package com.backend.komeet.notice.application;

import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.notice.model.entities.Notice;
import com.backend.komeet.notice.presentation.controller.NoticeRegisterRequest;
import com.backend.komeet.notice.repositories.NoticeRepository;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.backend.komeet.infrastructure.exception.ErrorCode.NOT_AN_ADMIN_USER;
import static com.backend.komeet.infrastructure.exception.ErrorCode.USER_INFO_NOT_FOUND;
import static com.backend.komeet.user.enums.UserRole.ROLE_ADMIN;

@Service
@RequiredArgsConstructor
public class NoticeRegisterService {
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 등록
     *
     * @param userSeq 사용자 고유번호
     * @param request 공지사항 등록 요청
     */
    public void registerNotice(Long userSeq,
                               NoticeRegisterRequest request) {
        if (!isUserAdmin(userSeq)) {
            throw new CustomException(NOT_AN_ADMIN_USER);
        }
        noticeRepository.save(Notice.from(userSeq, request));
    }

    /**
     * 사용자가 관리자가 아닌지 확인
     *
     * @param userSeq 사용자 고유번호
     * @return 사용자가 관리자가 아닌지 여부
     */
    private boolean isUserAdmin(Long userSeq) {
        return getUser(userSeq)
                .getUserRole()
                .equals(ROLE_ADMIN);
    }

    /**
     * 사용자 정보 조회
     *
     * @param userSeq 사용자 고유번호
     * @param userSeq 사용자 고유번호
     * @return 사용자 정보
     * @return 사용자 정보
     */
    private User getUser(Long userSeq) {
        return userRepository
                .findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }
}
