package com.example.backend_pj4.infrastructure.databases.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.enums.PaymentStatus;
import com.example.backend_pj4.infrastructure.databases.entities.TransactionEntity;

@Repository
public interface JpaTransactionRepository extends JpaRepository<TransactionEntity, String> {
    
    List<TransactionEntity> findByUserUserIdOrderByTransactionDateDesc(String userId);
    
    Optional<TransactionEntity> findByTransactionCode(String transactionCode);
    
    Optional<TransactionEntity> findByGatewayTransactionId(String gatewayTransactionId);
    
    List<TransactionEntity> findByPaymentStatus(PaymentStatus paymentStatus);
    
    List<TransactionEntity> findByPaymentStatusOrderByTransactionDateDesc(PaymentStatus paymentStatus);
    
    Page<TransactionEntity> findByUserUserId(String userId, Pageable pageable);
    
    Page<TransactionEntity> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);
    
    @Query("SELECT t FROM TransactionEntity t WHERE t.transactionDate BETWEEN ?1 AND ?2 ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    long countByUserUserId(String userId);
    
    long countByPaymentStatus(PaymentStatus paymentStatus);
    
    @Query("SELECT SUM(t.amount) FROM TransactionEntity t WHERE t.paymentStatus = 'COMPLETED'")
    java.math.BigDecimal sumTotalRevenue();
    
    @Query("SELECT SUM(t.amount) FROM TransactionEntity t WHERE t.paymentStatus = 'COMPLETED' AND t.transactionDate BETWEEN ?1 AND ?2")
    java.math.BigDecimal sumRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
