package com.backend.immilog.notice.application.services;

import com.backend.immilog.notice.application.command.NoticeUploadCommand;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.repositories.NoticeRepository;
import com.backend.immilog.notice.exception.NoticeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.backend.immilog.notice.exception.NoticeErrorCode.NOT_AN_ADMIN_USER;

@Service
@RequiredArgsConstructor
public class NoticeCreateService {
    private final NoticeRepository noticeRepository;

    @Transactional
    public void registerNotice(
            Long userSeq,
            String userRole,
            NoticeUploadCommand command
    ) {
        validateAdminUser(userRole);
        noticeRepository.saveEntity(Notice.of(userSeq, command));
    }

    private static void validateAdminUser(
            String userRole
    ) {
        if (!Objects.equals(userRole, "ROLE_ADMIN")) {
            throw new NoticeException(NOT_AN_ADMIN_USER);
        }
    }
}
