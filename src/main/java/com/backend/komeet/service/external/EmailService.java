package com.backend.komeet.service.external;

import com.backend.komeet.exception.CustomException;
import com.backend.komeet.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static com.backend.komeet.exception.ErrorCode.*;

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
     * @param htmlBody    이메일 내용
     * @throws RuntimeException 이메일 전송 중 발생하는 예외를 런타임 예외로 감싸서 던집니다.
     */
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("your-email@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);  // true는 HTML을 의미함
        }catch (Exception e){
            log.error("Failed to send email", e);
            throw new CustomException(EMAIL_SEND_FAILED);
        }

        javaMailSender.send(message);
    }
}

