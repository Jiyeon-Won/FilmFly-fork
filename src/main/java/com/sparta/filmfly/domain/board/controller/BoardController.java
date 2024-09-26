package com.sparta.filmfly.domain.board.controller;

import com.sparta.filmfly.domain.board.dto.*;
import com.sparta.filmfly.domain.board.service.BoardService;
import com.sparta.filmfly.global.auth.UserDetailsImpl;
import com.sparta.filmfly.global.common.response.DataResponseDto;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import com.sparta.filmfly.global.util.ResponseUtils;
import com.sparta.filmfly.global.util.PageUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;
    /**
     * 보드 생성
     */
    @PostMapping
    public ResponseEntity<DataResponseDto<BoardResponseDto>> createBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody BoardRequestDto requestDto
    ) {
        BoardResponseDto responseDto = boardService.createBoard(userDetails.getUser(),requestDto);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 보드 조회
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<DataResponseDto<BoardReactionResponseDto>> getBoard(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long boardId
    ) {
        BoardReactionResponseDto responseDto = boardService.getBoard(userDetails, boardId);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 보드 페이징 조회
     * http://localhost:8080/boards?page=1&filterGoodCount=0&filterHits=0&search=제목
     */
    @GetMapping
    public ResponseEntity<DataResponseDto<PageResponseDto<BoardPageDto>>> getPageBoard(
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
        @RequestParam(required = false, defaultValue = "false") boolean isAsc,
        @RequestParam(value = "filterGoodCount", required = false) final Long filterGoodCount,
        @RequestParam(value = "filterHits", required = false) final Long filterHits,
        @RequestParam(value = "search", required = false) final String search
    ) {
        Pageable pageable = PageUtils.of(page, size, sortBy, isAsc);
        PageResponseDto<BoardPageDto>  responseDto = boardService.getPageBoard(filterGoodCount,filterHits,search,pageable);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 유저의 보드 목록
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<DataResponseDto<PageResponseDto<BoardPageDto>>> getUsersBoard(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long userId,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
        @RequestParam(required = false, defaultValue = "false") boolean isAsc
    ) {
        Pageable pageable = PageUtils.of(page, size, sortBy, isAsc);
        PageResponseDto<BoardPageDto> responseDto = boardService.getUsersBoard(userDetails, userId, pageable);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 보드 수정 권한 확인
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{boardId}/update-permission")
    public ResponseEntity<DataResponseDto<Boolean>> getBoardUpdatePermission(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long boardId
    ) {
        Boolean response = boardService.getBoardUpdatePermission(userDetails.getUser(),boardId);
        return ResponseUtils.success(response);
    }

    /**
     * 보드 수정 페이지 정보
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{boardId}/for-update")
    public ResponseEntity<DataResponseDto<BoardUpdateResponseDto>> forUpdateBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long boardId
    ) {
        BoardUpdateResponseDto responseDto = boardService.forUpdateBoard(userDetails.getUser(),boardId);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 보드 수정
     */
    @PatchMapping("/{boardId}")
    public ResponseEntity<DataResponseDto<BoardResponseDto>> updateBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody BoardRequestDto requestDto,
            @PathVariable Long boardId
    ) {
        BoardResponseDto responseDto = boardService.updateBoard(userDetails.getUser(),requestDto,boardId);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 보드 삭제
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<DataResponseDto<String>> deleteBoard(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long boardId
    ) {
        String responseDto = boardService.deleteBoard(userDetails.getUser(),boardId);
        return ResponseUtils.success(responseDto);
    }
}