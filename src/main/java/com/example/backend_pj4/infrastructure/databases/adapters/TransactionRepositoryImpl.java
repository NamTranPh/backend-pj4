package com.example.backend_pj4.infrastructure.databases.adapters;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.enums.PaymentStatus;
import com.example.backend_pj4.domain.entities.Transaction;
import com.example.backend_pj4.domain.repository.TransactionRepository;
import com.example.backend_pj4.infrastructure.databases.mapper.TransactionMapper;
import com.example.backend_pj4.infrastructure.databases.repository.JpaTransactionRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final JpaTransactionRepository jpaTransactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Transaction save(Transaction transaction) {
        var entity = transactionMapper.toEntity(transaction);
        var saved = jpaTransactionRepository.save(entity);
        return transactionMapper.toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(String transactionId) {
        return jpaTransactionRepository.findById(transactionId)
                .map(transactionMapper::toDomain);
    }

    @Override
    public List<Transaction> findAll() {
        return jpaTransactionRepository.findAll().stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String transactionId) {
        jpaTransactionRepository.deleteById(transactionId);
    }

    @Override
    public List<Transaction> findByUserId(String userId) {
        return jpaTransactionRepository.findByUserUserIdOrderByTransactionDateDesc(userId).stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByUserIdOrderByTransactionDateDesc(String userId) {
        return jpaTransactionRepository.findByUserUserIdOrderByTransactionDateDesc(userId).stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Transaction> findByTransactionCode(String transactionCode) {
        return jpaTransactionRepository.findByTransactionCode(transactionCode)
                .map(transactionMapper::toDomain);
    }

    @Override
    public Optional<Transaction> findByGatewayTransactionId(String gatewayTransactionId) {
        return jpaTransactionRepository.findByGatewayTransactionId(gatewayTransactionId)
                .map(transactionMapper::toDomain);
    }

    @Override
    public List<Transaction> findByPaymentStatus(String paymentStatus) {
        return jpaTransactionRepository.findByPaymentStatus(PaymentStatus.valueOf(paymentStatus)).stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findPendingTransactions() {
        return jpaTransactionRepository.findByPaymentStatusOrderByTransactionDateDesc(PaymentStatus.PENDING).stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findCompletedTransactions() {
        return jpaTransactionRepository.findByPaymentStatusOrderByTransactionDateDesc(PaymentStatus.COMPLETED).stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findFailedTransactions() {
        return jpaTransactionRepository.findByPaymentStatusOrderByTransactionDateDesc(PaymentStatus.FAILED).stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByPaymentMethod(String paymentMethod) {
        return jpaTransactionRepository.findAll().stream()
                .filter(t -> t.getPaymentMethod() != null && t.getPaymentMethod().name().equals(paymentMethod))
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByUserIdAndPaymentMethod(String userId, String paymentMethod) {
        return jpaTransactionRepository.findByUserUserIdOrderByTransactionDateDesc(userId).stream()
                .filter(t -> t.getPaymentMethod() != null && t.getPaymentMethod().name().equals(paymentMethod))
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaTransactionRepository.findByDateRange(startDate, endDate).stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByCompletedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaTransactionRepository.findByDateRange(startDate, endDate).stream()
                .filter(t -> t.getCompletedAt() != null)
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByMembershipId(String membershipId) {
        return jpaTransactionRepository.findAll().stream()
                .filter(t -> t.getMembership() != null && t.getMembership().getMemberId().equals(membershipId))
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByPaymentStatus(String paymentStatus) {
        return jpaTransactionRepository.countByPaymentStatus(PaymentStatus.valueOf(paymentStatus));
    }

    @Override
    public long countByPaymentMethod(String paymentMethod) {
        return jpaTransactionRepository.findAll().stream()
                .filter(t -> t.getPaymentMethod() != null && t.getPaymentMethod().name().equals(paymentMethod))
                .count();
    }

    @Override
    public Double sumAmountByPaymentStatus(String paymentStatus) {
        var total = jpaTransactionRepository.findByPaymentStatus(PaymentStatus.valueOf(paymentStatus)).stream()
                .filter(t -> t.getAmount() != null)
                .mapToDouble(t -> t.getAmount().doubleValue())
                .sum();
        return total;
    }

    @Override
    public Double sumAmountByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        var revenue = jpaTransactionRepository.sumRevenueByDateRange(startDate, endDate);
        return revenue != null ? revenue.doubleValue() : 0.0;
    }
}
