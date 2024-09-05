package com.sparta.filmfly.domain.collection.controller;

import com.sparta.filmfly.domain.collection.dto.*;
import com.sparta.filmfly.domain.collection.service.CollectionService;
import com.sparta.filmfly.domain.movie.dto.MovieResponseDto;
import com.sparta.filmfly.global.auth.UserDetailsImpl;
import com.sparta.filmfly.global.common.response.DataResponseDto;
import com.sparta.filmfly.global.common.response.MessageResponseDto;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import com.sparta.filmfly.global.util.ResponseUtils;
import com.sparta.filmfly.global.util.PageUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    /**
     * 보관함 생성
     */
    @PostMapping
    public ResponseEntity<DataResponseDto<CollectionWithUserResponseDto>> createCollection(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CollectionRequestDto collectionRequestDto
    ) {
        CollectionWithUserResponseDto collectionResponseDto = collectionService.createCollection(userDetails.getUser(), collectionRequestDto);
        return ResponseUtils.success(collectionResponseDto);
    }

    /**
     * 보관함 단일 조회
     */
    @GetMapping("/{collectionId}")
    public ResponseEntity<DataResponseDto<CollectionWithUserResponseDto>> getCollection(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long collectionId
    ) {
        CollectionWithUserResponseDto collectionResponseDto = collectionService.getCollection(userDetails, collectionId);
        return ResponseUtils.success(collectionResponseDto);
    }

    /**
     * 자신의 보관함 목록 조회
     */
    @GetMapping
    public ResponseEntity<DataResponseDto<List<CollectionStatusResponseDto>>> getAllCollection(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) Long movieId
    ) {
        List<CollectionStatusResponseDto> collectionResponseDtoList = collectionService.getAllCollection(userDetails.getUser(),movieId);
        return ResponseUtils.success(collectionResponseDtoList);
    }

    /**
     * 유저의 보관함 목록
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<DataResponseDto<PageResponseDto<List<CollectionResponseDto>>>> getUsersCollections(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "false") boolean isAsc
    ) {
        Pageable pageable = PageUtils.of(page, size, sortBy, isAsc);
        PageResponseDto<List<CollectionResponseDto>> responseDto = collectionService.getUsersCollections(userId,pageable);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 보관함 상세 조회 _ 영화 목록 조회
     */
    @GetMapping("/{collectionId}/movies")
    public ResponseEntity<DataResponseDto<PageResponseDto<List<MovieResponseDto>>>> getMovieCollection(
            @PathVariable Long collectionId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "9") int size,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "false") boolean isAsc
    ) {
        Pageable pageable = PageUtils.of(page, size, sortBy, isAsc);
        PageResponseDto<List<MovieResponseDto>> movieCollectionResponseDto = collectionService.getMovieCollection(collectionId,pageable);
        return ResponseUtils.success(movieCollectionResponseDto);
    }

    /**
     * 해당 보관함 수정 권한 확인
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{collectionId}/update-permission")
    public ResponseEntity<DataResponseDto<Boolean>> getCollectionIdUpdatePermission(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long collectionId
    ) {
        Boolean response = collectionService.getCollectionIdUpdatePermission(userDetails.getUser(),collectionId);
        return ResponseUtils.success(response);
    }

    /**
     * 보관함 수정 페이지 정보
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{collectionId}/for-update")
    public ResponseEntity<DataResponseDto<CollectionResponseDto>> forUpdateCollection(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long collectionId
    ) {
        CollectionResponseDto responseDto = collectionService.forUpdateCollection(userDetails.getUser(),collectionId);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 보관함 수정
     */
    @PatchMapping("/{collectionId}")
    public ResponseEntity<DataResponseDto<CollectionResponseDto>> updateCollection(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CollectionRequestDto requestDto,
            @PathVariable Long collectionId
    ) {
        CollectionResponseDto responseDto = collectionService.updateCollection(userDetails.getUser(),requestDto,collectionId);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 보관함 삭제
     */
    @DeleteMapping("/{collectionId}")
    public ResponseEntity<MessageResponseDto> deleteCollection(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long collectionId
    ) {
        collectionService.deleteCollection(userDetails.getUser(), collectionId);
        return ResponseUtils.success();
    }

    /**
     * 보관함에 영화 등록
     */
    @PostMapping("/add-movie")
    public ResponseEntity<MessageResponseDto> createMovieCollection(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody MovieCollectionRequestDto movieCollectionRequestDto
    ) {
        collectionService.createMovieCollection(userDetails.getUser(), movieCollectionRequestDto);
        return ResponseUtils.success();
    }

    /**
     * 보관함 상세 조회 _ 영화 삭제
     */
    @DeleteMapping("/delete-movie")
    public ResponseEntity<MessageResponseDto> deleteMovieCollection(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody MovieCollectionRequestDto movieCollectionRequestDto
    ) {
        collectionService.deleteMovieCollection(userDetails.getUser(), movieCollectionRequestDto);
        return ResponseUtils.success();
    }
}