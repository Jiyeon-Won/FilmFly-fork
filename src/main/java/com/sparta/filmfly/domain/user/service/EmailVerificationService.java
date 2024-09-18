package com.sparta.filmfly.domain.user.service;

import com.sparta.filmfly.domain.user.repository.UserRepository;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.exception.custom.detail.AccessDeniedException;
import com.sparta.filmfly.global.exception.custom.detail.DuplicateException;
import com.sparta.filmfly.global.exception.custom.detail.LimitedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.MemcachedClient;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final MemcachedClient memcachedClient;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    private static final long EXPIRATION_TIME = 3 * 60; // 3분 (180초)
    private static final long SEND_LIMIT_RESET_TIME = 60 * 60; // 1시간 (3600초)
    private static final int MAX_SEND_COUNT = 5;

    /**
     * 이메일 인증 코드를 전송
     */
    @Transactional
    public void sendVerificationEmail(String email) {
        // 이메일 존재 여부 확인
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateException(ResponseCodeEnum.EMAIL_ALREADY_EXISTS);
        }

        // 전송 횟수 확인
        String sendCountKey = email + ":sendCount";
        Integer sendCount = (Integer) memcachedClient.get(sendCountKey);
        log.info("sendCount:"+sendCount);
        if (sendCount == null) {
            sendCount = 0;
        }

        if (sendCount >= MAX_SEND_COUNT) {
            throw new LimitedException(ResponseCodeEnum.EMAIL_RESEND_LIMIT);
        }

        // 인증 코드 생성
        String verificationCode = generateVerificationCode();

        // Memcached에 인증 코드 저장 (3분간 유효)
        memcachedClient.set(email, (int) EXPIRATION_TIME, verificationCode);

        // 전송 횟수 증가 및 갱신
        sendCount++;
        memcachedClient.set(sendCountKey, (int) SEND_LIMIT_RESET_TIME, sendCount);

        // 이메일 발송
        sendEmail(email, "이메일 인증 코드는 다음과 같습니다: " + verificationCode);

        // 인증 상태 초기화 (아직 인증되지 않음)
        String verificationStatusKey = email + ":verified";
        memcachedClient.set(verificationStatusKey, (int) EXPIRATION_TIME, false);
    }

    private void sendEmail(String to, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("이메일 인증 코드");
        message.setText(text);

        mailSender.send(message);
    }

    @Transactional
    public void verifyEmailCode(String email, String code) {
        String storedCode = (String) memcachedClient.get(email);

        if (storedCode == null) {
            throw new AccessDeniedException(ResponseCodeEnum.EMAIL_VERIFICATION_REQUIRED);
        }

        if (!storedCode.equals(code)) {
            throw new AccessDeniedException(ResponseCodeEnum.EMAIL_VERIFICATION_TOKEN_MISMATCH);
        }

        // 인증 성공 후, Memcached에서 코드 삭제 및 인증 상태 업데이트
        memcachedClient.delete(email);
        String verificationStatusKey = email + ":verified";
        memcachedClient.set(verificationStatusKey, (int) EXPIRATION_TIME, true);
    }

    @Transactional
    public void checkIfEmailVerified(String email) {
        String verificationStatusKey = email + ":verified";
        Boolean isVerified = (Boolean) memcachedClient.get(verificationStatusKey);

        if (isVerified == null || !isVerified) {
            throw new AccessDeniedException(ResponseCodeEnum.EMAIL_VERIFICATION_REQUIRED);
        }
    }

    private String generateVerificationCode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}