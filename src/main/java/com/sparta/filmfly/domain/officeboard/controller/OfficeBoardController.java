package com.sparta.filmfly.domain.officeboard.controller;

import com.sparta.filmfly.domain.officeboard.dto.OfficeBoardPageResponseDto;
import com.sparta.filmfly.domain.officeboard.dto.OfficeBoardRequestDto;
import com.sparta.filmfly.domain.officeboard.dto.OfficeBoardResponseDto;
import com.sparta.filmfly.domain.officeboard.dto.OfficeBoardUpdateResponseDto;
import com.sparta.filmfly.domain.officeboard.service.OfficeBoardService;
import com.sparta.filmfly.global.auth.UserDetailsImpl;
import com.sparta.filmfly.global.common.response.DataResponseDto;
import com.sparta.filmfly.global.util.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/officeboards")

public class OfficeBoardController {

    private final OfficeBoardService officeBoardService;

    /**
     * 운영보드 생성
     */
    @PostMapping
    public ResponseEntity<DataResponseDto<OfficeBoardResponseDto>> createOfficeBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody OfficeBoardRequestDto requestDto
    ) {
        OfficeBoardResponseDto responseDto = officeBoardService.createOfficeBoard(userDetails.getUser(),requestDto);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 운영보드 단일 조회
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<DataResponseDto<OfficeBoardResponseDto>> getOfficeBoard(
            @PathVariable Long boardId
    ) {
        OfficeBoardResponseDto responseDto = officeBoardService.getOfficeBoard(boardId);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 운영보드 전체 조회
     */
    @GetMapping
    public ResponseEntity<DataResponseDto<OfficeBoardPageResponseDto>> getPageOfficeBoards(
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer pageNum,
            @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size
            //@RequestParam(value = "search", required = false) final String search   필요하면 추가하기
    ) {
        OfficeBoardPageResponseDto  responseDto = officeBoardService.getPageOfficeBoards(pageNum,size);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 보드 수정 페이지 정보
     */
    @GetMapping("/{boardId}/for-update")
    public ResponseEntity<DataResponseDto<OfficeBoardUpdateResponseDto>> forUpdateOfficeBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long boardId
    ) {
        OfficeBoardUpdateResponseDto responseDto = officeBoardService.forUpdateOfficeBoard(userDetails.getUser(),boardId);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 운영보드 수정
     */
    @PatchMapping("/{boardId}")
    public ResponseEntity<DataResponseDto<OfficeBoardResponseDto>> updateOfficeBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody OfficeBoardRequestDto requestDto,
            @PathVariable Long boardId
    ) {
        OfficeBoardResponseDto responseDto = officeBoardService.updateOfficeBoard(userDetails.getUser(),requestDto,boardId);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 운영보드 삭제
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<DataResponseDto<String>> deleteOfficeBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long boardId
    ) {
        String responseDto = officeBoardService.deleteOfficeBoard(userDetails.getUser(), boardId);
        return ResponseUtils.success(responseDto);
    }

}