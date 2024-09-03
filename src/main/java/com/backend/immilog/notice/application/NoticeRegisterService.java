package com.backend.immilog.notice.application;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.notice.exception.NoticeException;
import com.backend.immilog.notice.model.entities.Notice;
import com.backend.immilog.notice.model.repositories.NoticeRepository;
import com.backend.immilog.notice.presentation.request.NoticeRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.backend.immilog.global.enums.UserRole.ROLE_ADMIN;
import static com.backend.immilog.notice.exception.NoticeErrorCode.NOT_AN_ADMIN_USER;

@Service
@RequiredArgsConstructor
public class NoticeRegisterService {
    private final NoticeRepository noticeRepository;

    @Transactional
    public void registerNotice(
            Long userSeq,
            UserRole userRole,
            NoticeRegisterRequest request
    ) {
        validateAdminUser(userRole);
        noticeRepository.save(Notice.of(userSeq, request));
    }

    private static void validateAdminUser(
            UserRole userRole
    ) {
        if (!Objects.equals(userRole, ROLE_ADMIN)) {
            throw new NoticeException(NOT_AN_ADMIN_USER);
        }
    }
}
