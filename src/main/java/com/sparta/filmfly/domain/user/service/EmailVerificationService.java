package com.sparta.filmfly.domain.user.service;

import com.sparta.filmfly.domain.user.repository.UserRepository;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.exception.custom.detail.AccessDeniedException;
import com.sparta.filmfly.global.exception.custom.detail.AsyncException;
import com.sparta.filmfly.global.exception.custom.detail.DuplicateException;
import com.sparta.filmfly.global.exception.custom.detail.LimitedException;
import com.sparta.filmfly.global.infra.AsyncEmailService;
import com.sparta.filmfly.global.infra.RedisService;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final RedisService redisService;
    private final UserRepository userRepository;
    private final AsyncEmailService asyncEmailService;

    private static final long EXPIRATION_TIME = 3 * 60; // 3분 (180초)
    private static final long SEND_LIMIT_RESET_TIME = 60 * 60; // 1시간 (3600초)
    private static final int MAX_SEND_COUNT = 5;

    /**
     * 이메일 인증 코드를 전송
     */
    public void sendVerificationEmail(String email) {
        // 이메일 존재 여부 확인
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateException(ResponseCodeEnum.EMAIL_ALREADY_EXISTS);
        }

        // 전송 횟수 확인
        String sendCountKey = email + ":sendCount";
        Integer sendCount = redisService.get(sendCountKey, Integer.class);
        if (sendCount == null) {
            sendCount = 0;
        }

        if (sendCount >= MAX_SEND_COUNT) {
            throw new LimitedException(ResponseCodeEnum.EMAIL_RESEND_LIMIT);
        }

        // 인증 코드 생성
        String verificationCode = generateVerificationCode();

        // Memcached에 인증 코드 저장 (3분간 유효)
        redisService.set(email, verificationCode, EXPIRATION_TIME, TimeUnit.SECONDS);

        // 전송 횟수 증가 및 갱신
        ++sendCount;
        redisService.set(sendCountKey, sendCount, SEND_LIMIT_RESET_TIME, TimeUnit.SECONDS);

        // 이메일 발송
        String emailText = "이메일 인증 코드는 다음과 같습니다: " + verificationCode;
        this.sendEmail(email, emailText);

        // 인증 상태 초기화 (아직 인증되지 않음)
        String verificationStatusKey = email + ":verified";
        redisService.set(verificationStatusKey, false, 10, TimeUnit.MINUTES);
    }

    public void verifyEmailCode(String email, String code) {
        String storedCode = redisService.get(email, String.class);

        if (storedCode == null || storedCode.isBlank()) {
            throw new AccessDeniedException(ResponseCodeEnum.EMAIL_VERIFICATION_REQUIRED);
        }

        if (!storedCode.equals(code)) {
            throw new AccessDeniedException(ResponseCodeEnum.EMAIL_VERIFICATION_TOKEN_MISMATCH);
        }

        // 인증 성공 후, 인증코드 삭제 및 인증 상태 업데이트
        redisService.delete(email);
        String verificationStatusKey = email + ":verified";
        redisService.set(verificationStatusKey, true, 5, TimeUnit.MINUTES);
    }

    public void checkIfEmailVerified(String email) {
        String verificationStatusKey = email + ":verified";
        Boolean isVerified = redisService.get(verificationStatusKey, Boolean.class);

        if (isVerified == null || !isVerified) {
            throw new AccessDeniedException(ResponseCodeEnum.EMAIL_VERIFICATION_REQUIRED);
        }
    }

    private void sendEmail(String email, String emailText) {
        asyncEmailService.sendEmail(email, "이메일 인증 코드", emailText)
            .thenAccept(isEmailSent -> {
                if (Boolean.FALSE.equals(isEmailSent)) {
                    log.error("이메일 전송 실패: {}", email);
                    throw new AsyncException(ResponseCodeEnum.EMAIL_VERIFICATION_SEND_FAILED);
                }
            })
            .exceptionally(ex -> {
                log.error("이메일 전송 중 예외 발생: {}", email, ex);
                throw new AsyncException(ResponseCodeEnum.EMAIL_VERIFICATION_SEND_FAILED);
            });
    }

    private String generateVerificationCode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}