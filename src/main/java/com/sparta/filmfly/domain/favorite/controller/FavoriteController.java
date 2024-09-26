package com.sparta.filmfly.domain.favorite.controller;

import com.sparta.filmfly.domain.favorite.dto.FavoriteResponseDto;
import com.sparta.filmfly.domain.favorite.service.FavoriteService;
import com.sparta.filmfly.domain.movie.dto.MovieResponseDto;
import com.sparta.filmfly.global.auth.UserDetailsImpl;
import com.sparta.filmfly.global.common.response.DataResponseDto;
import com.sparta.filmfly.global.common.response.MessageResponseDto;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import com.sparta.filmfly.global.util.ResponseUtils;
import com.sparta.filmfly.global.util.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
    * 찜 등록하기
    */
    @PostMapping("/movies/{movieId}")
    public ResponseEntity<DataResponseDto<FavoriteResponseDto>> createFavorite(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long movieId
    ) {
        FavoriteResponseDto responseDto = favoriteService.createFavorite(userDetails.getUser(), movieId);
        return ResponseUtils.success(responseDto);
    }

    /**
    * 찜 조회하기
    */
    @GetMapping("/users/{userId}")
    public ResponseEntity<DataResponseDto<PageResponseDto<MovieResponseDto>>> getPageFavorite(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "9") int size,
            @RequestParam(required = false, defaultValue = "false") boolean isAsc
    ) {
        Pageable pageable = PageUtils.of(page, size, "id", isAsc);
        PageResponseDto<MovieResponseDto> movieResponseDtoList = favoriteService.getPageFavorite(userId,pageable);
        return ResponseUtils.success(movieResponseDtoList);
    }

    /**
    * 찜 취소하기
    */
    @DeleteMapping("/movies/{movieId}")
    public ResponseEntity<MessageResponseDto> deleteFavorite(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long movieId
    ) {
        favoriteService.deleteFavorite(userDetails.getUser(), movieId);
        return ResponseUtils.success();
    }
}