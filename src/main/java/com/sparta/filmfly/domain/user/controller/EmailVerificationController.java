package com.sparta.filmfly.domain.user.controller;

import com.sparta.filmfly.domain.user.dto.EmailVerificationRequestDto;
import com.sparta.filmfly.domain.user.service.EmailVerificationService;
import com.sparta.filmfly.global.common.response.MessageResponseDto;
import com.sparta.filmfly.global.util.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    /**
     * 이메일 인증 코드 전송
     */
    @PostMapping("/code-send")
    public ResponseEntity<MessageResponseDto> sendVerificationCode(@Valid @RequestBody EmailVerificationRequestDto requestDto) {
        emailVerificationService.sendVerificationEmail(requestDto.getEmail());
        return ResponseUtils.success();
    }

    /**
     * 이메일 인증 코드 검증
     */
    @PostMapping("/verify")

    public ResponseEntity<MessageResponseDto> verifyCode(@Valid @RequestBody EmailVerificationRequestDto requestDto) {
        emailVerificationService.verifyEmailCode(requestDto.getEmail(), requestDto.getCode());
        return ResponseUtils.success();
    }


}