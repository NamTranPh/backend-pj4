package com.example.backend_pj4.application.services.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.common.enums.PaymentMethod;
import com.example.backend_pj4.common.enums.PaymentStatus;
import com.example.backend_pj4.domain.entities.Membership;
import com.example.backend_pj4.domain.entities.Transaction;
import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.repository.MembershipRepository;
import com.example.backend_pj4.domain.repository.TransactionRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService extends BaseService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;

    public Transaction createTransaction(String userId, String membershipId, BigDecimal amount,
            PaymentMethod paymentMethod, String notes) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Membership membership = null;
        if (membershipId != null) {
            membership = membershipRepository.findById(membershipId)
                    .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));
        }

        String transactionCode = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Transaction transaction = Transaction.builder()
                .amount(amount)
                .paymentMethod(paymentMethod)
                .paymentStatus(PaymentStatus.PENDING)
                .transactionCode(transactionCode)
                .notes(notes)
                .transactionDate(LocalDateTime.now())
                .build();

        transaction.setUser(user);
        transaction.setMembership(membership);

        return transactionRepository.save(transaction);
    }

    public Transaction completeTransaction(String transactionId, String gatewayTransactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        transaction.setPaymentStatus(PaymentStatus.COMPLETED);
        transaction.setGatewayTransactionId(gatewayTransactionId);
        transaction.setCompletedAt(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    public Transaction failTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        transaction.setPaymentStatus(PaymentStatus.FAILED);
        return transactionRepository.save(transaction);
    }

    public Transaction refundTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (transaction.getPaymentStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only completed transactions can be refunded");
        }

        transaction.setPaymentStatus(PaymentStatus.REFUNDED);
        transaction.setRefundedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactions(String userId) {
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public Optional<Transaction> getTransactionByCode(String code) {
        return transactionRepository.findByTransactionCode(code);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getPendingTransactions() {
        return transactionRepository.findPendingTransactions();
    }

    @Transactional(readOnly = true)
    public List<Transaction> getCompletedTransactions() {
        return transactionRepository.findCompletedTransactions();
    }

    @Transactional(readOnly = true)
    public Double getTotalRevenue() {
        Double revenue = transactionRepository.sumAmountByPaymentStatus("COMPLETED");
        return revenue != null ? revenue : 0.0;
    }

    @Transactional(readOnly = true)
    public Double getRevenueByDateRange(LocalDateTime start, LocalDateTime end) {
        Double revenue = transactionRepository.sumAmountByDateRange(start, end);
        return revenue != null ? revenue : 0.0;
    }

    @Transactional(readOnly = true)
    public long countByStatus(String status) {
        return transactionRepository.countByPaymentStatus(status);
    }
}
