package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.backend_pj4.common.constants.enums.PaymentOrderStatus;
import com.example.backend_pj4.infrastructure.database.entities.PaymentOrderJpaEntity;

public interface SpringDataPaymentOrderRepository extends JpaRepository<PaymentOrderJpaEntity, String> {
    List<PaymentOrderJpaEntity> findByUser_Id(String userId);
    Optional<PaymentOrderJpaEntity> findByTransactionCode(String transactionCode);
    Optional<PaymentOrderJpaEntity> findByGatewayTransactionId(String gatewayTransactionId);
    List<PaymentOrderJpaEntity> findByStatus(PaymentOrderStatus status);
    List<PaymentOrderJpaEntity> findByUser_IdAndStatus(String userId, PaymentOrderStatus status);
    @Query("SELECT p FROM PaymentOrderJpaEntity p WHERE p.status = 'PENDING'")
    List<PaymentOrderJpaEntity> findPendingOrders();
    long countByStatus(PaymentOrderStatus status);
}


