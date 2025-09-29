package com.example.backend_pj4.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.entities.Transaction;

public interface TransactionRepository {
    // Basic CRUD
    Transaction save(Transaction transaction);

    Optional<Transaction> findById(Integer transactionId);

    List<Transaction> findAll();

    void deleteById(Integer transactionId);

    // User transactions
    List<Transaction> findByUserId(Integer userId);

    List<Transaction> findByUserIdOrderByTransactionDateDesc(Integer userId);

    // Transaction code
    Optional<Transaction> findByTransactionCode(String transactionCode);

    Optional<Transaction> findByGatewayTransactionId(String gatewayTransactionId);

    // Status queries
    List<Transaction> findByPaymentStatus(String paymentStatus);

    List<Transaction> findPendingTransactions();

    List<Transaction> findCompletedTransactions();

    List<Transaction> findFailedTransactions();

    // Payment method queries
    List<Transaction> findByPaymentMethod(String paymentMethod);

    List<Transaction> findByUserIdAndPaymentMethod(Integer userId, String paymentMethod);

    // Date range queries
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByCompletedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Membership transactions
    List<Transaction> findByMembershipId(Integer membershipId);

    // Statistics
    long countByPaymentStatus(String paymentStatus);

    long countByPaymentMethod(String paymentMethod);

    Double sumAmountByPaymentStatus(String paymentStatus);

    Double sumAmountByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
