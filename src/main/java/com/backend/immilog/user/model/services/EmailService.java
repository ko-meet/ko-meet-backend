package com.backend.immilog.user.model.services;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    @Async
    void sendHtmlEmail(
            String to,
            String subject,
            String htmlBody
    );
}
