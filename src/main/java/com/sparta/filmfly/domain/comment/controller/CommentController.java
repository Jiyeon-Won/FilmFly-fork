package com.sparta.filmfly.domain.comment.controller;

import com.sparta.filmfly.domain.comment.dto.CommentRequestDto;
import com.sparta.filmfly.domain.comment.dto.CommentResponseDto;
import com.sparta.filmfly.domain.comment.dto.CommentUpdateResponseDto;
import com.sparta.filmfly.domain.comment.service.CommentService;
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
@RequestMapping
public class CommentController {
    private final CommentService commentService;

    /**
     * 댓글 생성
     */
    @PostMapping("/boards/{boardId}/comments")
    public ResponseEntity<DataResponseDto<CommentResponseDto>> createComment(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody CommentRequestDto requestDto,
        @PathVariable Long boardId
    ) {
        CommentResponseDto responseDto = commentService.createComment(userDetails.getUser(),requestDto,boardId);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 댓글 조회
     */
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<DataResponseDto<CommentResponseDto>> getComment(
        @PathVariable Long commentId
    ) {
        CommentResponseDto responseDto = commentService.getComment(commentId);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 댓글 페이지 조회
     * http://localhost:8080/boards/{boardId}/comments?page=1&size=10
     */
    @GetMapping("/boards/{boardId}/comments")
    public ResponseEntity<DataResponseDto<PageResponseDto<List<CommentResponseDto>>>> gerPageComment(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long boardId,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
        @RequestParam(required = false, defaultValue = "false") boolean isAsc
    ) {
        Pageable pageable = PageUtils.of(page, size, sortBy, isAsc);
        PageResponseDto<List<CommentResponseDto>> responseDto = commentService.gerPageComment(userDetails, boardId, pageable);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 유저의 댓글 목록
     */
    @GetMapping("/comments/users/{userId}")
    public ResponseEntity<DataResponseDto<PageResponseDto<List<CommentResponseDto>>>> getUsersComments(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long userId,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
        @RequestParam(required = false, defaultValue = "false") boolean isAsc
    ) {
        Pageable pageable = PageUtils.of(page, size, sortBy, isAsc);
        PageResponseDto<List<CommentResponseDto>> responseDto = commentService.getUsersComments(userDetails, userId, pageable);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 댓글 수정 권한 확인
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comments/{commentId}/update-permission")
    public ResponseEntity<DataResponseDto<Boolean>> getCommentUpdatePermission(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId
    ) {
        Boolean response = commentService.getCommentUpdatePermission(userDetails.getUser(),commentId);
        return ResponseUtils.success(response);
    }

    /**
     * 댓글 수정 정보
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comments/{commentId}/for-update")
    public ResponseEntity<DataResponseDto<CommentUpdateResponseDto>> forUpdateComment(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long commentId
    ) {
        CommentUpdateResponseDto response = commentService.forUpdateComment(userDetails.getUser(),commentId);
        return ResponseUtils.success(response);
    }

    /**
     * 댓글 수정
     */
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<DataResponseDto<CommentResponseDto>> updateComment(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody CommentRequestDto requestDto,
        @PathVariable Long commentId
    ) {
        CommentResponseDto responseDto = commentService.updateComment(userDetails.getUser(),requestDto,commentId);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<DataResponseDto<String>> deleteComment(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long commentId
    ) {
        String responseDto = commentService.deleteComment(userDetails.getUser(),commentId);
        return ResponseUtils.success(responseDto);
    }
}