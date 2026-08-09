package com.example.backend_pj4.infrastructure.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.infrastructure.config.properties.AuthCookieProperties;
import com.example.backend_pj4.infrastructure.config.properties.JwtProperties;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Quản lý cookie refresh token. Access token KHÔNG đặt vào cookie nữa —
 * nó được trả trong body (client gắn Authorization: Bearer). Chỉ refresh token
 * nằm trong cookie HTTP-only, tách theo luồng user / admin.
 */
@Component
@RequiredArgsConstructor
public class AuthCookieService {
    private final AuthCookieProperties cookieProperties;
    private final JwtProperties jwtProperties;

    // ---------- USER ----------
    public void addUserRefreshCookie(HttpServletResponse response, String refreshToken) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(
                cookieProperties.getRefreshName(),
                refreshToken,
                cookieProperties.getRefreshPath(),
                jwtProperties.getRefreshExpiration() / 1000).toString());
    }

    public void clearUserRefreshCookie(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(
                cookieProperties.getRefreshName(),
                "",
                cookieProperties.getRefreshPath(),
                0).toString());
    }

    // ---------- ADMIN ----------
    public void addAdminRefreshCookie(HttpServletResponse response, String refreshToken) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(
                cookieProperties.getAdminRefreshName(),
                refreshToken,
                cookieProperties.getAdminRefreshPath(),
                jwtProperties.getRefreshExpiration() / 1000).toString());
    }

    public void clearAdminRefreshCookie(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(
                cookieProperties.getAdminRefreshName(),
                "",
                cookieProperties.getAdminRefreshPath(),
                0).toString());
    }

    private ResponseCookie buildCookie(String name, String value, String path, long maxAgeSeconds) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .path(path)
                .maxAge(maxAgeSeconds);

        if (cookieProperties.getDomain() != null && !cookieProperties.getDomain().isBlank()) {
            builder.domain(cookieProperties.getDomain());
        }

        return builder.build();
    }
}
