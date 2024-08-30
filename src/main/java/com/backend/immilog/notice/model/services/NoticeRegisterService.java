package com.backend.immilog.notice.model.services;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.notice.presentation.request.NoticeRegisterRequest;
import org.springframework.transaction.annotation.Transactional;

public interface NoticeRegisterService {
    @Transactional
    void registerNotice(
            Long userSeq,
            UserRole userRole,
            NoticeRegisterRequest request
    );
}
