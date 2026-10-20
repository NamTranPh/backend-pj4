package com.example.backend_pj4.application.port.in.auth;

/** đăng xuất tất cả các thiết bị khác, giữ lại thiết bị hiện tại. */
public interface RevokeOtherSessionsUseCase {
    void execute(String userId, String rawRefreshToken);
}
