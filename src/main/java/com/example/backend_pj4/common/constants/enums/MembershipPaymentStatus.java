// Enum định nghĩa các trạng thái thanh toán gói hội viên (Chờ, Đã thanh toán, Thất bại, Đã hoàn tiền).
package com.example.backend_pj4.common.constants.enums;

public enum MembershipPaymentStatus {
    PENDING,
    PAID,
    FAILED,
    REFUNDED
}
