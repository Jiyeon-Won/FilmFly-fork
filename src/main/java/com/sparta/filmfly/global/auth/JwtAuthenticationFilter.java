package com.sparta.filmfly.global.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.filmfly.domain.user.dto.UserLoginRequestDto;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.domain.user.entity.UserStatusEnum;
import com.sparta.filmfly.domain.user.repository.UserRepository;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.util.ResponseUtils;
import com.sparta.filmfly.global.exception.custom.detail.*;
import com.sparta.filmfly.global.util.CookieUtils;
import com.sparta.filmfly.global.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j(topic = "로그인 처리 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(
        JwtUtils jwtUtils, UserRepository userRepository, ObjectMapper objectMapper
    ) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        setFilterProcessesUrl("/users/login");
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request, HttpServletResponse response
    ) throws AuthenticationException {
        try {
            UserLoginRequestDto requestDto = objectMapper.readValue(request.getInputStream(), UserLoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                    requestDto.getUsername(), requestDto.getPassword(), null
                )
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(
        HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult
    ) throws IOException {
        User user = userRepository.findByUsernameOrElseThrow(authResult.getName());

        if (user.getUserStatus() == UserStatusEnum.SUSPENDED) {
            CookieUtils.deleteCookie(response, "accessToken");
            CookieUtils.deleteCookie(response, "refreshToken");
            throw new SuspendedException(ResponseCodeEnum.USER_SUSPENDED);
        }

        String accessToken = jwtUtils.createAccessToken(user.getUsername());
        String refreshToken = jwtUtils.createRefreshToken(user.getUsername());

        CookieUtils.addCookie(response, "accessToken", accessToken, JwtUtils.ACCESS_TOKEN_TIME);
        CookieUtils.addCookie(response, "refreshToken", accessToken, JwtUtils.REFRESH_TOKEN_TIME);

        user.updateRefreshToken(refreshToken);

        writeResponse(response, HttpStatus.OK, "로그인 성공");
    }

    @Override
    protected void unsuccessfulAuthentication(
        HttpServletRequest request, HttpServletResponse response, AuthenticationException failed
    ) throws IOException {
        writeResponse(response, ResponseCodeEnum.LOGIN_FAILED.getHttpStatus(), ResponseCodeEnum.LOGIN_FAILED.getMessage());
    }

    private void writeResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        String body = objectMapper.writeValueAsString(
            ResponseUtils.of(status, message)
        );

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(body);
    }

}