package com.sparta.filmfly.global.exception;

import com.sparta.filmfly.global.common.response.DataResponseDto;
import com.sparta.filmfly.global.common.response.MessageResponseDto;
import com.sparta.filmfly.global.util.ResponseUtils;
import com.sparta.filmfly.global.exception.custom.GlobalException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<MessageResponseDto> handleExamException(GlobalException e) {
        log.error("에러: ", e);
        return ResponseUtils.of(e.getExamCodeEnum().getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponseDto> handleException(Exception e) {
        log.error("서버 에러: ", e);
        return ResponseUtils.of(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponseDto<List<String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errorMessageList = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach(v -> errorMessageList.add(v.getDefaultMessage()));
        log.error("유효성 검사 실패:\n{}", String.join(",\n", errorMessageList));
        return ResponseUtils.of(HttpStatus.BAD_REQUEST, "유효성 검사 실패", errorMessageList);
    }
}