package com.sparta.filmfly.global.auth;

import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.domain.user.repository.UserRepository;
import com.sparta.filmfly.global.util.CookieUtils;
import com.sparta.filmfly.global.util.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;

    public JwtAuthorizationFilter(
        JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService, UserRepository userRepository
    ) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String http = req.getMethod();
        String uri = req.getRequestURI();
        System.out.printf("요청된 URI: %s %s\n", http, uri);

        String accessToken = CookieUtils.getCookie(req, "accessToken");
        if (!StringUtils.hasText(accessToken)) {
            filterChain.doFilter(req, res);
            return;
        }

        // accessToken 검사
        if (jwtUtils.validateToken(accessToken)) {
            // SecurityContext에 Authentication 객체 추가
            String username = jwtUtils.getClaimsFromToken(accessToken).getSubject();
            setAuthentication(username);
        } else {
            // refreshToken 검사
            String refreshToken = CookieUtils.getCookie(req, "refreshToken");
            if (!StringUtils.hasText(refreshToken) || !jwtUtils.validateToken(refreshToken)) {
                log.warn("refreshToken이 유효하지 않음 or 없음");
                return;
            }

            Claims claimsFromRefreshToken = jwtUtils.getClaimsFromToken(refreshToken);
            String username = claimsFromRefreshToken.getSubject();

            Optional<User> findUser = userRepository.findByUsername(username);
            findUser.ifPresent(user -> {
                if (refreshToken.equals(user.getRefreshToken())) {
                    // accessToken은 항상 재발급
                    // refreshToken은 만료 시간이 1일 남은 경우 재발급
                    handleTokenReissue(username, res, claimsFromRefreshToken);

                    setAuthentication(username);
                }
            });
        }

        filterChain.doFilter(req, res);
    }

    private void handleTokenReissue(String username, HttpServletResponse res, Claims refreshTokenClaims) {
        // accessToken은 항상 재발급
        String newAccessToken = jwtUtils.createAccessToken(username);
        CookieUtils.addCookie(res, "accessToken", newAccessToken, JwtUtils.ACCESS_TOKEN_TIME);

        // refreshToken은 만료 시간이 1일 남은 경우 재발급
        long expirationTime = refreshTokenClaims.getExpiration().getTime();
        long currentTime = System.currentTimeMillis();
        long oneDayMillis = 24 * 60 * 60 * 1000;

        // 만료 시간이 1일 이내로 남았을 경우 새로운 Refresh Token 생성
        if (expirationTime - currentTime < oneDayMillis) {
            String newRefreshToken = jwtUtils.createRefreshToken(username);
            CookieUtils.addCookie(res, "refreshToken", newRefreshToken, JwtUtils.REFRESH_TOKEN_TIME);
        }
    }

    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}