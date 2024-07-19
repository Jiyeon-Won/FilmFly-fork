package com.sparta.filmfly.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCodeEnum {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "신고내용을 찾을 수 없습니다."),
    BLOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "차단내용을 찾을 수 없습니다."),



    ;
    private final HttpStatus httpStatus;
    private final String message;
}