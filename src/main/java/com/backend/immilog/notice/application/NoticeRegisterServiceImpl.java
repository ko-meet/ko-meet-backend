package com.backend.immilog.notice.application;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.notice.model.entities.Notice;
import com.backend.immilog.notice.model.repositories.NoticeRepository;
import com.backend.immilog.notice.model.services.NoticeRegisterService;
import com.backend.immilog.notice.presentation.request.NoticeRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.backend.immilog.global.enums.UserRole.ROLE_ADMIN;
import static com.backend.immilog.notice.exception.NoticeErrorCode.NOT_AN_ADMIN_USER;

@Service
@RequiredArgsConstructor
public class NoticeRegisterServiceImpl implements NoticeRegisterService {
    private final NoticeRepository noticeRepository;

    @Override
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
            throw new CustomException(NOT_AN_ADMIN_USER);
        }
    }
}