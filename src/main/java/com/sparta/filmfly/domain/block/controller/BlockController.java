package com.sparta.filmfly.domain.block.controller;

import com.sparta.filmfly.domain.block.dto.BlockRequestDto;
import com.sparta.filmfly.domain.block.dto.BlockedUserResponseDto;
import com.sparta.filmfly.domain.block.service.BlockService;
import com.sparta.filmfly.global.auth.UserDetailsImpl;
import com.sparta.filmfly.global.common.response.DataResponseDto;
import com.sparta.filmfly.global.common.response.MessageResponseDto;
import com.sparta.filmfly.global.common.response.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    // 유저 차단
    @PostMapping
    public ResponseEntity<MessageResponseDto> blockUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Validated BlockRequestDto blockRequestDto
    ) {
        blockService.blockUser(userDetails.getUser().getId(), blockRequestDto);
        return ResponseUtils.success();
    }

    // 차단된 유저 목록 조회
    @GetMapping
    public ResponseEntity<DataResponseDto<List<BlockedUserResponseDto>>> getBlockedUsers(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<BlockedUserResponseDto> blockedUsers = blockService.getBlockedUsers(userDetails.getUser());
        return ResponseUtils.success(blockedUsers);
    }

    // 차단 해제
    @DeleteMapping
    public ResponseEntity<MessageResponseDto> unblockUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Validated BlockRequestDto blockRequestDto
    ) {
        blockService.unblockUser(userDetails.getUser().getId(), blockRequestDto.getBlockedId());
        return ResponseUtils.success();
    }
}
