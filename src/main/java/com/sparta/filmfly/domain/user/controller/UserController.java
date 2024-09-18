package com.sparta.filmfly.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.filmfly.domain.user.dto.UserDeleteRequestDto;
import com.sparta.filmfly.domain.user.dto.UserNicknameCheckRequestDto;
import com.sparta.filmfly.domain.user.dto.UserPasswordUpdateRequestDto;
import com.sparta.filmfly.domain.user.dto.UserProfileUpdateRequestDto;
import com.sparta.filmfly.domain.user.dto.UserResponseDto;
import com.sparta.filmfly.domain.user.dto.UserSignupRequestDto;
import com.sparta.filmfly.domain.user.service.KakaoService;
import com.sparta.filmfly.domain.user.service.UserService;
import com.sparta.filmfly.global.auth.UserDetailsImpl;
import com.sparta.filmfly.global.common.response.DataResponseDto;
import com.sparta.filmfly.global.common.response.MessageResponseDto;
import com.sparta.filmfly.global.util.CookieUtils;
import com.sparta.filmfly.global.util.ResponseUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
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
        userService.deleteUser(userDetails.getUser(), requestDto);

        CookieUtils.deleteCookie(response, "accessToken");
        CookieUtils.deleteCookie(response, "refreshToken");

        SecurityContextHolder.clearContext();
        return ResponseUtils.success();
    }

    /**
     * 본인 활성화 시키기(탈퇴 상태일때)
     */
    @PatchMapping("/activate")
    public ResponseEntity<DataResponseDto<UserResponseDto>> activateUser(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UserResponseDto userResponseDto = userService.activateUser(userDetails.getUser());
        return ResponseUtils.success(userResponseDto);
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