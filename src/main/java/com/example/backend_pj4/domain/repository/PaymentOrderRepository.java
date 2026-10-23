package com.example.backend_pj4.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.PaymentOrder;

/**
 * Đơn thanh toán.
 */
public interface PaymentOrderRepository {
    PaymentOrder save(PaymentOrder order);

    Optional<PaymentOrder> findById(String id);

    Optional<PaymentOrder> findByTransactionCode(String transactionCode);

    PageResult<PaymentOrder> findByUserId(String userId, PageQuery query);

    /** Đơn còn treo của một cổng thanh toán — job đối soát quét theo mốc thời gian. */
    List<PaymentOrder> findPendingByGatewaySince(String gateway, LocalDateTime since, int limit);

    void deleteById(String id);

    /**
     * Chuyển đơn PENDING -> COMPLETED bằng một câu UPDATE nguyên tử.
     *
     * @return 1 nếu luồng gọi giành được quyền quyết toán, 0 nếu luồng khác đã làm trước
     */
    int markCompletedIfPending(String orderId, String gatewayTransactionId, LocalDateTime now);

    /** Chuyển đơn PENDING -> FAILED bằng một câu UPDATE nguyên tử. */
    int markFailedIfPending(String orderId, String reason, LocalDateTime now);
}
