package com.example.backend_pj4.presentation.auth;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Trích thông tin thiết bị từ HTTP request để lưu kèm phiên đăng nhập.
 * Thuộc tầng presentation vì nó đọc trực tiếp HttpServletRequest — use case chỉ nhận
 * hai chuỗi đã bóc sẵn qua command.
 */
final class ClientInfo {

    /** Độ dài cột user_agent trong bảng refresh_token. */
    private static final int MAX_USER_AGENT_LENGTH = 512;

    private ClientInfo() {}

    static String userAgent(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (ua == null || ua.isBlank()) {
            return null;
        }
        return ua.length() > MAX_USER_AGENT_LENGTH ? ua.substring(0, MAX_USER_AGENT_LENGTH) : ua;
    }

    /**
     * Sau reverse proxy thì getRemoteAddr() trả IP của proxy, nên ưu tiên X-Forwarded-For
     * (phần tử đầu là client gốc). Header này client tự đặt được, nên chỉ dùng để hiển thị
     * cho người dùng, không dùng làm căn cứ phân quyền.
     */
    static String ipAddress(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            String first = forwarded.split(",")[0].trim();
            if (!first.isEmpty()) {
                return first.length() > 45 ? first.substring(0, 45) : first;
            }
        }
        return request.getRemoteAddr();
    }
}
