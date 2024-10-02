package com.sparta.filmfly.domain.reaction.controller;

import com.sparta.filmfly.domain.reaction.dto.BadRequestDto;
import com.sparta.filmfly.domain.reaction.dto.BadResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionBoardResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionCommentResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionMovieResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionReviewResponseDto;
import com.sparta.filmfly.domain.reaction.service.BadService;
import com.sparta.filmfly.global.auth.UserDetailsImpl;
import com.sparta.filmfly.global.common.response.DataResponseDto;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import com.sparta.filmfly.global.util.ResponseUtils;
import com.sparta.filmfly.global.util.PageUtils;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bads")
public class BadController {

    private final BadService badService;

    /**
     * 싫어요 추가
     */
    @PostMapping
    public ResponseEntity<DataResponseDto<BadResponseDto>> addBad(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody BadRequestDto requestDto
    ) {
        BadResponseDto responseDto = badService.addBad(userDetails.getUser(), requestDto);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 싫어요 취소
     */
    @DeleteMapping
    public ResponseEntity<DataResponseDto<BadResponseDto>> removeBad(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody BadRequestDto requestDto
    ) {
        BadResponseDto responseDto = badService.removeBad(userDetails.getUser(), requestDto);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 사용자가 좋아요를 누른 영화 조회
     */
    @GetMapping("/movies/users/{userId}")
    public ResponseEntity<DataResponseDto<PageResponseDto<ReactionMovieResponseDto>>> getPageGoodMovie(
        @PathVariable Long userId,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "id") String sortBy,
        @RequestParam(required = false, defaultValue = "false") boolean isAsc
    ) {
        Pageable pageable = PageUtils.of(page, size, sortBy, isAsc);
        PageResponseDto<ReactionMovieResponseDto> responseDto = badService.getPageBadMovie(userId, pageable);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 사용자가 좋아요를 누른 리뷰 조회
     */
    @GetMapping("/reviews/users/{userId}")
    public ResponseEntity<DataResponseDto<PageResponseDto<ReactionReviewResponseDto>>> getPageGoodReview(
        @PathVariable Long userId,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "id") String sortBy,
        @RequestParam(required = false, defaultValue = "false") boolean isAsc
    ) {
        Pageable pageable = PageUtils.of(page, size, sortBy, isAsc);
        PageResponseDto<ReactionReviewResponseDto> responseDto = badService.getPageBadReview(userId, pageable);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 사용자가 좋아요를 누른 게시물 조회
     */
    @GetMapping("/boards/users/{userId}")
    public ResponseEntity<DataResponseDto<PageResponseDto<ReactionBoardResponseDto>>> getPageGoodBoard(
        @PathVariable Long userId,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "id") String sortBy,
        @RequestParam(required = false, defaultValue = "false") boolean isAsc
    ) {
        Pageable pageable = PageUtils.of(page, size, sortBy, isAsc);
        PageResponseDto<ReactionBoardResponseDto> responseDto = badService.getPageBadBoard(userId, pageable);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 사용자가 좋아요를 누른 댓글 조회
     */
    @GetMapping("/comments/users/{userId}")
    public ResponseEntity<DataResponseDto<PageResponseDto<ReactionCommentResponseDto>>> getPageGoodComment(
        @PathVariable Long userId,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "id") String sortBy,
        @RequestParam(required = false, defaultValue = "false") boolean isAsc
    ) {
        Pageable pageable = PageUtils.of(page, size, sortBy, isAsc);
        PageResponseDto<ReactionCommentResponseDto> responseDto = badService.getPageBadComment(userId, pageable);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 싫어요 누른 유저인지 확인
     */
    @PostMapping("/check")
    public ResponseEntity<DataResponseDto<Boolean>> checkBadByUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody BadRequestDto requestDto
    ) {
        Boolean check = badService.checkBadByUser(userDetails.getUser(), requestDto);
        return ResponseUtils.success(check);
    }
}