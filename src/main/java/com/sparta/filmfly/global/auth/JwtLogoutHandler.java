package com.sparta.filmfly.global.auth;

import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.domain.user.repository.UserRepository;
import com.sparta.filmfly.global.util.CookieUtils;
import com.sparta.filmfly.global.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Override
    public void logout(
        HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) {
        String accessToken = CookieUtils.getCookie(request, "accessToken");
        String refreshToken = CookieUtils.getCookie(request, "refreshToken");
        if (accessToken != null && refreshToken != null) {
            String username = jwtUtils.getClaimsFromToken(accessToken).getSubject();
            User findUser = userRepository.findByUsernameOrElseThrow(username);
            findUser.deleteRefreshToken();
            userRepository.save(findUser);
        }

        CookieUtils.deleteCookie(response, "accessToken");
        CookieUtils.deleteCookie(response, "refreshToken");

        SecurityContextHolder.clearContext();
    }
}