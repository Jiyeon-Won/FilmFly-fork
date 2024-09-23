package com.sparta.filmfly.global.infra;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public CompletableFuture<Boolean> sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            msg.setFrom(fromEmail);

            mailSender.send(msg);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("\n|{}|\n이메일 전송 실패 - 받는 사람: {}, 제목: {}, 에러 메시지: {}", fromEmail, to, subject, e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }
}