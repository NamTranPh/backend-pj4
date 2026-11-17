package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.backend_pj4.infrastructure.database.entities.PaymentRefundJpaEntity;

public interface PaymentRefundJpaRepository extends 
        JpaRepository<PaymentRefundJpaEntity, String>,
        JpaSpecificationExecutor<PaymentRefundJpaEntity> {

    List<PaymentRefundJpaEntity> findByPaymentOrder_IdOrderByCreatedAtDesc(String paymentOrderId);
}
