package com.backend.immilog.user.model.services;

import com.backend.immilog.user.application.command.UserReportCommand;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

public interface UserReportService {
    @Async
    @Transactional
    void reportUser(
            Long targetUserSeq,
            Long reporterUserSeq,
            UserReportCommand reportUserCommand
    );
}
