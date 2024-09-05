package com.sparta.filmfly.domain.reaction.controller;

import com.sparta.filmfly.domain.reaction.dto.BadRequestDto;
import com.sparta.filmfly.domain.reaction.dto.GoodRequestDto;
import com.sparta.filmfly.domain.reaction.dto.GoodResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionBoardResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionCommentResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionMovieResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionReviewResponseDto;
import com.sparta.filmfly.domain.reaction.service.GoodService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/goods")
public class GoodController {

    private final GoodService goodService;

    /**
     * 좋아요 추가
     */
    @PostMapping
    public ResponseEntity<DataResponseDto<GoodResponseDto>> addGood(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody GoodRequestDto requestDto
    ) {
        GoodResponseDto responseDto = goodService.addGood(userDetails.getUser(), requestDto);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 좋아요 취소
     */
    @DeleteMapping
    public ResponseEntity<DataResponseDto<GoodResponseDto>> removeGood(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody GoodRequestDto requestDto
    ) {
        GoodResponseDto responseDto = goodService.removeGood(userDetails.getUser(), requestDto);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 사용자가 좋아요를 누른 영화 조회
     */
    @GetMapping("/movies/users/{userId}")
    public ResponseEntity<DataResponseDto<PageResponseDto<List<ReactionMovieResponseDto>>>> getPageGoodMovie(
        @PathVariable Long userId,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "id") String sortBy,
        @RequestParam(required = false, defaultValue = "false") boolean isAsc
    ) {
        Pageable pageable = PageUtils.of(page, size, sortBy, isAsc);
        PageResponseDto<List<ReactionMovieResponseDto>> responseDto = goodService.getPageGoodMovie(userId, pageable);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 사용자가 좋아요를 누른 리뷰 조회
     */
    @GetMapping("/reviews/users/{userId}")
    public ResponseEntity<DataResponseDto<PageResponseDto<List<ReactionReviewResponseDto>>>> getPageGoodReview(
        @PathVariable Long userId,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "id") String sortBy,
        @RequestParam(required = false, defaultValue = "false") boolean isAsc
    ) {
        Pageable pageable = PageUtils.of(page, size, sortBy, isAsc);
        PageResponseDto<List<ReactionReviewResponseDto>> responseDto = goodService.getPageGoodReview(userId, pageable);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 사용자가 좋아요를 누른 게시물 조회
     */
    @GetMapping("/boards/users/{userId}")
    public ResponseEntity<DataResponseDto<PageResponseDto<List<ReactionBoardResponseDto>>>> getPageGoodBoard(
        @PathVariable Long userId,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "id") String sortBy,
        @RequestParam(required = false, defaultValue = "false") boolean isAsc
    ) {
        Pageable pageable = PageUtils.of(page, size, sortBy, isAsc);
        PageResponseDto<List<ReactionBoardResponseDto>> responseDto = goodService.getPageGoodBoard(userId, pageable);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 사용자가 좋아요를 누른 댓글 조회
     */
    @GetMapping("/comments/users/{userId}")
    public ResponseEntity<DataResponseDto<PageResponseDto<List<ReactionCommentResponseDto>>>> getPageGoodComment(
        @PathVariable Long userId,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "id") String sortBy,
        @RequestParam(required = false, defaultValue = "false") boolean isAsc
    ) {
        Pageable pageable = PageUtils.of(page, size, sortBy, isAsc);
        PageResponseDto<List<ReactionCommentResponseDto>> responseDto = goodService.getPageGoodComment(userId, pageable);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 좋아요 누른 유저인지 확인
     */
    @PostMapping("/check")
    public ResponseEntity<DataResponseDto<Boolean>> checkGoodByUser(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody BadRequestDto requestDto
    ) {

        Boolean check = goodService.checkGoodByUser(userDetails.getUser(), requestDto);
        return ResponseUtils.success(check);
    }
}