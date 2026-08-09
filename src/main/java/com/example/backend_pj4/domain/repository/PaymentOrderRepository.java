package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.common.constants.enums.PaymentOrderStatus;
import com.example.backend_pj4.domain.model.PaymentOrder;

public interface PaymentOrderRepository {
    PaymentOrder save(PaymentOrder order);
    Optional<PaymentOrder> findById(String id);
    List<PaymentOrder> findAll();
    void deleteById(String id);
    List<PaymentOrder> findByUserId(String userId);
    Optional<PaymentOrder> findByTransactionCode(String transactionCode);
    Optional<PaymentOrder> findByGatewayTransactionId(String gatewayTransactionId);
    List<PaymentOrder> findByStatus(PaymentOrderStatus status);
    List<PaymentOrder> findByUserIdAndStatus(String userId, PaymentOrderStatus status);
    List<PaymentOrder> findPendingOrders();
    long countByStatus(PaymentOrderStatus status);
}
