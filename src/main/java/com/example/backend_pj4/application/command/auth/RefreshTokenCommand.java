package com.example.backend_pj4.application.command.auth;

/** userAgent/ipAddress cập nhật lại thông tin thiết bị của phiên mỗi lần rotation. */
public record RefreshTokenCommand(String rawRefreshToken, String userAgent, String ipAddress) {}
