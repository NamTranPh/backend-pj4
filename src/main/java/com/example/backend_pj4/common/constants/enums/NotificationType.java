// Enum định nghĩa các loại thông báo gửi tới người dùng (Báo cáo mới, Báo cáo được giải quyết, Tập phim mới, Hết hạn gói hội viên, Bình luận trả lời, Hệ thống).
package com.example.backend_pj4.common.constants.enums;

public enum NotificationType {
    NEW_REPORT,
    REPORT_RESOLVED,
    NEW_EPISODE,
    MEMBERSHIP_EXPIRY,
    COMMENT_REPLY,
    SYSTEM
}
