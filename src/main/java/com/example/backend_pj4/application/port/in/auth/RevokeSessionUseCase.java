package com.example.backend_pj4.application.port.in.auth;

/** Cổng GHI: đăng xuất một phiên (một thiết bị). */
public interface RevokeSessionUseCase {

    /** Chỉ thu hồi được phiên thuộc về chính người dùng gọi. */
    void execute(String userId, String sessionId);
}
