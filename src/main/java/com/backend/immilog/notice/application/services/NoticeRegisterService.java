package com.backend.immilog.notice.application.services;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.notice.application.command.NoticeUploadCommand;
import com.backend.immilog.notice.exception.NoticeException;
import com.backend.immilog.notice.model.entities.Notice;
import com.backend.immilog.notice.model.repositories.NoticeRepository;
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
            NoticeUploadCommand command
    ) {
        validateAdminUser(userRole);
        noticeRepository.save(Notice.of(userSeq, command));
    }

    private static void validateAdminUser(
            UserRole userRole
    ) {
        if (!Objects.equals(userRole, ROLE_ADMIN)) {
            throw new NoticeException(NOT_AN_ADMIN_USER);
        }
    }
}
