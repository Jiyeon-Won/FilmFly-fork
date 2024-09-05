package com.sparta.filmfly.global.auth;

import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.domain.user.repository.UserRepository;
import com.sparta.filmfly.global.util.CookieUtils;
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

    @Override
    public void logout(
        HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        user.deleteRefreshToken();
        userRepository.save(user);

        CookieUtils.deleteCookie(response, "accessToken");
        CookieUtils.deleteCookie(response, "refreshToken");

        SecurityContextHolder.clearContext();
    }
}