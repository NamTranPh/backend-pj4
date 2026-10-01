package com.example.backend_pj4.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class RefreshToken {
    private String id;
    private String userId;

    /** Đổi mỗi lần rotation (15 phút/lần). Định danh một tờ vé cụ thể. */
    private String tokenId;

    /**
     * Cố định xuyên suốt một phiên đăng nhập, rotation giữ nguyên. Không có nó thì
     * ~96 tokenId sinh ra mỗi ngày là rời rạc, không nối lại thành "một thiết bị" được.
     */
    private String sessionId;

    private boolean admin;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt;

    /** Thông tin thiết bị, phục vụ màn hình quản lý phiên đăng nhập. */
    private String userAgent;
    private String ipAddress;
    private LocalDateTime lastUsedAt;

    private LocalDateTime createdAt;
}
