package com.sparta.filmfly.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.web.util.WebUtils;

public abstract class CookieUtils {

    /**
     * 쿠키 생성
     * @param response  응답 객체(HttpServletResponse)
     * @param name      쿠키 이름
     * @param value     쿠키에 저장할 값(JWT 토큰 등)
     * @param maxAge    쿠키의 유효 기간(ms 단위)
     */
    public static void addCookie(HttpServletResponse response, String name, String value, long maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
            .path("/")
            .sameSite("None")
            .secure(true)
            .maxAge(maxAge)
            .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void addCookie(HttpServletResponse response, String name, String value, long maxAge, boolean isHttpOnly) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
            .path("/")
            .httpOnly(isHttpOnly)
            .sameSite("None")
            .secure(true)
            .maxAge(maxAge)
            .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * 쿠키 가져오기
     * @param request  요청 객체(HttpServletRequest)
     * @param name     가져올 쿠키의 이름
     * @return         쿠키의 값, 쿠키가 없을 경우 null 반환
     */
    public static String getCookie(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * 쿠키 삭제
     * @param response  응답 객체(HttpServletResponse)
     * @param name      삭제할 쿠키의 이름
     */
    public static void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}