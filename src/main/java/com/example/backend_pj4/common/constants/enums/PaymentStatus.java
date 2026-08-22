// Enum định nghĩa trạng thái giao dịch thanh toán tổng quát (Chờ, Đã hoàn thành, Thất bại, Đã hoàn tiền).
package com.example.backend_pj4.common.constants.enums;

public enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}
