package com.example.backend_pj4.infrastructure.database.adapters;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;
import com.example.backend_pj4.infrastructure.database.mappers.PaymentOrderPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.PaymentOrderJpaRepository;

@Repository
public class JpaPaymentOrderRepositoryAdapter implements PaymentOrderRepository {

    private final PaymentOrderJpaRepository jpaRepository;
    private final PaymentOrderPersistenceMapper mapper;

    public JpaPaymentOrderRepositoryAdapter(PaymentOrderJpaRepository jpaRepository,
                                            PaymentOrderPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public PaymentOrder save(PaymentOrder order) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(order)));
    }

    @Override
    public Optional<PaymentOrder> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<PaymentOrder> findByTransactionCode(String transactionCode) {
        return jpaRepository.findByTransactionCode(transactionCode).map(mapper::toDomain);
    }

    @Override
    public List<PaymentOrder> findPendingByGatewaySince(String gateway, LocalDateTime since, int limit) {
        return jpaRepository.findPendingByGatewaySince(gateway, since, limit).stream()
                .map(mapper::toDomain).limit(limit).toList();
    }

    @Override
    @Transactional
    public int markCompletedIfPending(String orderId, String gatewayTransactionId, LocalDateTime now) {
        return jpaRepository.markCompletedIfPending(orderId, gatewayTransactionId, now);
    }

    @Override
    @Transactional
    public int markFailedIfPending(String orderId, String reason, LocalDateTime now) {
        return jpaRepository.markFailedIfPending(orderId, reason, now);
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }
}
