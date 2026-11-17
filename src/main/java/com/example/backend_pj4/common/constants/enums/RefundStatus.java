/**
 * Enum định nghĩa trạng thái của phiếu hoàn tiền
 */

package com.example.backend_pj4.common.constants.enums;

public enum RefundStatus {
    REQUESTED,
    PROCESSING,
    COMPLETED,
    CANCELLED,
    REJECTED // Admin từ chối hoàn tiền (do vi phạm chính sách) -> Giữ nguyên VIP
}
