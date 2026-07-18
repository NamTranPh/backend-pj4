package com.example.backend_pj4.infrastructure.databases.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.entities.Transaction;
import com.example.backend_pj4.infrastructure.databases.entities.TransactionEntity;

@Component
public class TransactionMapper {

    private final UserMapper userMapper;
    private final MembershipMapper membershipMapper;

    public TransactionMapper(UserMapper userMapper, @Lazy MembershipMapper membershipMapper) {
        this.userMapper = userMapper;
        this.membershipMapper = membershipMapper;
    }

    public Transaction toDomain(TransactionEntity entity) {
        if (entity == null) return null;

        return Transaction.builder()
                .transactionId(entity.getTransactionId())
                .user(userMapper.toDomain(entity.getUser()))
                .membership(entity.getMembership() != null
                        ? membershipMapper.toSimpleDomain(entity.getMembership())
                        : null)
                .amount(entity.getAmount())
                .paymentMethod(entity.getPaymentMethod())
                .paymentStatus(entity.getPaymentStatus())
                .transactionCode(entity.getTransactionCode())
                .gatewayTransactionId(entity.getGatewayTransactionId())
                .transactionDate(entity.getTransactionDate())
                .completedAt(entity.getCompletedAt())
                .refundedAt(entity.getRefundedAt())
                .notes(entity.getNotes())
                .build();
    }

    public TransactionEntity toEntity(Transaction domain) {
        if (domain == null) return null;

        TransactionEntity entity = new TransactionEntity();
        entity.setTransactionId(domain.getTransactionId());
        entity.setUser(userMapper.toEntity(domain.getUser()));
        entity.setMembership(domain.getMembership() != null
                ? membershipMapper.toEntity(domain.getMembership())
                : null);
        entity.setAmount(domain.getAmount());
        entity.setPaymentMethod(domain.getPaymentMethod());
        entity.setPaymentStatus(domain.getPaymentStatus());
        entity.setTransactionCode(domain.getTransactionCode());
        entity.setGatewayTransactionId(domain.getGatewayTransactionId());
        entity.setTransactionDate(domain.getTransactionDate());
        entity.setCompletedAt(domain.getCompletedAt());
        entity.setRefundedAt(domain.getRefundedAt());
        entity.setNotes(domain.getNotes());
        return entity;
    }

    public List<Transaction> toDomainList(List<TransactionEntity> entities) {
        if (entities == null || entities.isEmpty()) return List.of();
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<TransactionEntity> toEntityList(List<Transaction> domains) {
        if (domains == null || domains.isEmpty()) return List.of();
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
