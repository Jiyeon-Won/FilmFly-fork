package com.sparta.filmfly.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.filmfly.domain.user.dto.UserKakaoInfoDto;
import com.sparta.filmfly.domain.user.dto.UserResponseDto;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.domain.user.entity.UserRoleEnum;
import com.sparta.filmfly.domain.user.entity.UserStatusEnum;
import com.sparta.filmfly.domain.user.repository.UserRepository;
import com.sparta.filmfly.global.util.JwtUtils;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.exception.custom.detail.DuplicateException;
import com.sparta.filmfly.global.exception.custom.detail.NotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.SecureRandom;

@Slf4j(topic = "KAKAO Login")
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    public UserResponseDto kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        String accessToken = getToken(code);
        log.info("Kakao access token: {}", accessToken);

        UserKakaoInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
        log.info("Kakao user info: {}", kakaoUserInfo);

        return createOrUpdateUser(kakaoUserInfo, response);
    }

    /**
     * Access Token 획득
     */
    private String getToken(String code) throws JsonProcessingException {
        log.info("Kakao access code: {}", code);
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    /**
     * Kakao 사용자 정보 획득
     */
    private UserKakaoInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String pictureUrl = null;
        if (jsonNode.get("properties").get("profile_image") != null) {
            pictureUrl = jsonNode.get("properties").get("profile_image").asText();
        }

        return UserKakaoInfoDto.builder()
                .id(id)
                .nickname(generateRandomNickname(email))
                .pictureUrl(pictureUrl)
                .email(email)
                .build();
    }

    /**
     * 사용자 생성 또는 업데이트
     */
    public UserResponseDto createOrUpdateUser(UserKakaoInfoDto kakaoUserInfo, HttpServletResponse response) {
        String email = kakaoUserInfo.getEmail();
        User user;
        boolean isNewUser = false;

        try {
            user = userRepository.findByUsernameOrElseThrow(email);
        } catch (NotFoundException e) {
            // 신규 사용자 생성 시에만 이메일 중복 체크 수행
            if (userRepository.existsByEmail(kakaoUserInfo.getEmail())) {
                throw new DuplicateException(ResponseCodeEnum.EMAIL_ALREADY_EXISTS);
            }

            user = User.builder()
                    .username(email)
                    .password("")
                    .email(email)
                    .nickname(kakaoUserInfo.getNickname())
                    .pictureUrl(kakaoUserInfo.getPictureUrl())
                    .kakaoId(kakaoUserInfo.getId())
                    .userStatus(UserStatusEnum.ACTIVE)
                    .userRole(UserRoleEnum.ROLE_USER)
                    .build();
            userRepository.save(user);
            isNewUser = true;
        }

        String accessToken = jwtUtils.createAccessToken(user.getUsername());
        String refreshToken = jwtUtils.createRefreshToken(user.getUsername());

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        addCookie(response, "accessToken", accessToken);
        addCookie(response, "refreshToken", refreshToken);

        if (isNewUser) {
            return UserResponseDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .introduce(user.getIntroduce())
                    .pictureUrl(user.getPictureUrl())
                    .userRole(user.getUserRole())
                    .userStatus(user.getUserStatus())
                    .createdAt(user.getCreatedAt())
                    .build();
        }

        return null;
    }

    /**
     * 쿠키 추가
     */
    private void addCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value.replace(" ", "+"));
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 랜덤 닉네임 생성
     */
    private String generateRandomNickname(String email) {
        String randomString = generateRandomString();
        return email.split("@")[0] + "_" + randomString;
    }

    /**
     * 랜덤 문자열 생성
     */
    private String generateRandomString() {
        int length = 8;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}