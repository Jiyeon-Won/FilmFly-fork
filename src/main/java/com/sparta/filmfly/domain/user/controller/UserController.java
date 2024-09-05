package com.sparta.filmfly.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.filmfly.domain.user.dto.*;
import com.sparta.filmfly.domain.user.service.KakaoService;
import com.sparta.filmfly.domain.user.service.UserService;
import com.sparta.filmfly.global.auth.UserDetailsImpl;
import com.sparta.filmfly.global.common.response.DataResponseDto;
import com.sparta.filmfly.global.common.response.MessageResponseDto;
import com.sparta.filmfly.global.common.response.ResponseUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<DataResponseDto<UserResponseDto>> signup(@Valid @RequestBody UserSignupRequestDto requestDto) {
        UserResponseDto responseDto = userService.signup(requestDto);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 카카오 로그인 요청
     */
    @GetMapping("/kakao/authorize")
    public void redirectToKakaoAuthorize(HttpServletResponse response) throws IOException {
        String requestUrl = String.format(
                "https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                clientId, redirectUri
        );
        response.sendRedirect(requestUrl);
    }

    /**
     * 카카오 콜백 처리
     */
    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoLogin(
            @RequestParam String code,
            HttpServletResponse response
    ) throws JsonProcessingException {
        log.info("kakao login code: {}", code);
        UserResponseDto userResponseDto = kakaoService.kakaoLogin(code, response);
        if (userResponseDto != null) {
            return ResponseUtils.success(userResponseDto);
        } else {
            return ResponseUtils.success();
        }
    }

    /**
     * 비밀번호 변경
     */
    @PatchMapping("/password")
    public ResponseEntity<MessageResponseDto> updatePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserPasswordUpdateRequestDto requestDto
    ) {
        userService.updatePassword(userDetails.getUser(), requestDto);
        return ResponseUtils.success();
    }

    /**
     * 프로필 업로드
     */
    @PatchMapping("/profile")
    public ResponseEntity<DataResponseDto<UserResponseDto>> updateProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestPart("profileUpdateRequestDto") UserProfileUpdateRequestDto requestDto,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture
    ) {
        UserResponseDto responseDto = userService.updateProfile(userDetails.getUser(), requestDto, profilePicture);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 닉네임 중복 확인
     */
    @PostMapping("/check-nickname")
    public ResponseEntity<MessageResponseDto> checkNicknameDuplication(
        @Valid @RequestBody UserNicknameCheckRequestDto requestDto
    ) {
        userService.checkNicknameDuplication(requestDto.getNickname());
        return ResponseUtils.success();
    }

    /**
     * 프로필 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<DataResponseDto<UserResponseDto>> getProfile(@PathVariable Long userId) {
        UserResponseDto profile = userService.getProfile(userId);
        return ResponseUtils.success(profile);
    }

    /**
     * 본인 프로필 조회
     */
    @GetMapping
    public ResponseEntity<DataResponseDto<UserResponseDto>> getMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UserResponseDto profile = userService.getProfile(userDetails.getUserId());
        return ResponseUtils.success(profile);
    }

    /**
     * 회원탈퇴
     */
    @DeleteMapping("/withdraw")
    public ResponseEntity<MessageResponseDto> deleteUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserDeleteRequestDto requestDto,
            HttpServletResponse response
    ) {
        log.info("deleteUser");
        userService.deleteUser(userDetails.getUser(), requestDto);

        // 쿠키를 무효화하여 삭제
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);

        SecurityContextHolder.clearContext();
        return ResponseUtils.success();
    }

    /**
     * 본인 활성화 시키기(탈퇴 상태일때)
     */
    @PatchMapping("/activate")
    public ResponseEntity<DataResponseDto<UserResponseDto>> activateUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserResponseDto userResponseDto = userService.activateUser(userDetails.getUser());
        return ResponseUtils.success(userResponseDto);
    }

    /**
     * 본인 데이터인지 확인
     */
    @PostMapping("/check-owner")
    public ResponseEntity<DataResponseDto<List<UserOwnerCheckResponseDto>>> checkOwner(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UserOwnerCheckRequestDto requestDto
    ) {
        List<UserOwnerCheckResponseDto> responseDtos = userService.checkOwner(userDetails.getUser(), requestDto);
        return ResponseUtils.success(responseDtos);
    }

    /**
     * 본인 정보 확인 위한 API (ID, 닉네임, 한줄 소개, 프로필 Url)
     * front 마이페이지 연동 위해 API 추가했습니다.
     */
    @GetMapping("/myInfo")
    public ResponseEntity<DataResponseDto<UserResponseDto>> getMyUserInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        UserResponseDto responseDto = userService.getMyUserInfo(userDetails.getUser());
        return ResponseUtils.success(responseDto);
    }

}