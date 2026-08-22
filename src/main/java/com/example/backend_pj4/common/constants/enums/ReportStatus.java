// Enum định nghĩa trạng thái xử lý báo cáo vi phạm (Đang chờ, Đang xem xét, Đã giải quyết, Đã từ chối).
package com.example.backend_pj4.common.constants.enums;

public enum ReportStatus {
    PENDING,
    REVIEWING,
    RESOLVED,
    REJECTED
}
