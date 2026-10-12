// Enum định nghĩa trạng thái đơn đặt hàng thanh toán (Chờ, Đã hoàn thành, Thất bại, Đã hủy).
package com.example.backend_pj4.common.constants.enums;

public enum PaymentOrderStatus {
    PENDING,
    COMPLETED,
    FAILED,
    CANCELLED
    // REFUNDED  // Đã hoàn tiền - chưa dùng, thêm cùng luồng hoàn tiền (refundedAt, refundAmount, thu hồi Membership)
}
