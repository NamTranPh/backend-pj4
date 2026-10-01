package com.example.backend_pj4.domain.repository;

import java.util.Optional;

import com.example.backend_pj4.domain.model.RefreshToken;

/**
 * Nguồn sự thật duy nhất của refresh token. Trước đây có thêm port
 * {@code RefreshTokenStore} bọc một lớp Redis ở giữa, nhưng lớp đó không tự trả lời
 * được câu hỏi nào (Redis chỉ lưu userId, thiếu revokedAt và isAdmin) nên mọi lần
 * đọc vẫn phải xuống DB — đã bỏ.
 */
public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByTokenId(String tokenId);
    void revokeByTokenId(String tokenId);
    void revokeAllByUserId(String userId);
    /** Thu hồi toàn bộ token của một phiên — dùng cho "đăng xuất thiết bị này". */
    void revokeAllBySessionId(String sessionId);
    /** @return số row đã xoá, để job ghi log. */
    int deleteExpired();
}
