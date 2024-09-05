package com.sparta.filmfly.domain.test;

import com.sparta.filmfly.global.common.response.DataResponseDto;
import com.sparta.filmfly.global.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class TestController {

    @GetMapping("/")
    public ResponseEntity<String> test0() {
        log.info("Request : /");
        return ResponseEntity.ok("요청 성공 (~˘▾˘)~");
    }

    @GetMapping("/test/1")
    public ResponseEntity<String> test1() {
        log.info("test1");
        return ResponseEntity.ok("첫번째 테스트를 성공하셨습니다.");
    }

    @GetMapping("/test/2")
    public ResponseEntity<String> test2() {
        log.info("test2");
        return ResponseEntity.ok("이것은 테스트2의 성공인 것이야!");
    }

    @GetMapping("/test/3")
    public ResponseEntity<DataResponseDto<String>> test3() {
        log.info("test3");
        return ResponseUtils.success("테스트3의 성공");
    }
}