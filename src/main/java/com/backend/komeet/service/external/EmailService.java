package com.backend.komeet.service.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 이메일 발송 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    /**
     * 비동기적으로 이메일을 발송
     *
     * @param to      수신자 이메일 주소
     * @param subject 이메일 제목
     * @param text    이메일 내용
     * @throws RuntimeException 이메일 전송 중 발생하는 예외를 런타임 예외로 감싸서 던집니다.
     */
    @Async
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }
}
