package com.backend.immilog.user.application;

import com.backend.immilog.user.application.services.EmailServiceImpl;
import com.backend.immilog.user.model.services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

import static org.mockito.Mockito.*;

@DisplayName("이메일 서비스 테스트")
class EmailServiceTest {
    @Mock
    private JavaMailSender javaMailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailServiceImpl(javaMailSender);
    }

    @Test
    @DisplayName("이메일 발송 - 성공")
    void sendHtmlEmail_success() {
        // given
        String to = "to";
        String subject = "subject";
        String htmlBody = "htmlBody";
        MimeMessage message = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(message);

        // when
        emailService.sendHtmlEmail(to, subject, htmlBody);

        // then
        verify(javaMailSender, times(1)).send(message);
    }

}


