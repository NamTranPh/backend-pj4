package com.example.backend_pj4.application.dto.auth;

import java.time.LocalDateTime;

/**
 * Một phiên đăng nhập để hiển thị ở màn hình quản lý thiết bị.
 *
 * @param current true nếu đây chính là phiên đang gửi request — client dùng để gắn nhãn
 *                "Thiết bị này" và ẩn nút đăng xuất.
 */
public record SessionResult(
        String sessionId,
        String userAgent,
        String ipAddress,
        boolean admin,
        boolean current,
        LocalDateTime lastUsedAt,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {}
