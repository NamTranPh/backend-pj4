package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.constants.enums.PaymentOrderStatus;
import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;
import com.example.backend_pj4.infrastructure.database.mappers.PaymentOrderPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.PaymentOrderJpaRepository;

@Repository
public class JpaPaymentOrderRepositoryAdapter implements PaymentOrderRepository {

    private final PaymentOrderJpaRepository jpaRepository;
    private final PaymentOrderPersistenceMapper mapper;

    public JpaPaymentOrderRepositoryAdapter(PaymentOrderJpaRepository jpaRepository, PaymentOrderPersistenceMapper mapper) {
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
    public List<PaymentOrder> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<PaymentOrder> findByUserId(String userId) {
        return jpaRepository.findByUser_Id(userId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<PaymentOrder> findByTransactionCode(String transactionCode) {
        return jpaRepository.findByTransactionCode(transactionCode).map(mapper::toDomain);
    }

    @Override
    public Optional<PaymentOrder> findByGatewayTransactionId(String gatewayTransactionId) {
        return jpaRepository.findByGatewayTransactionId(gatewayTransactionId).map(mapper::toDomain);
    }

    @Override
    public List<PaymentOrder> findByStatus(PaymentOrderStatus status) {
        return jpaRepository.findByStatus(status).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<PaymentOrder> findByUserIdAndStatus(String userId, PaymentOrderStatus status) {
        return jpaRepository.findByUser_IdAndStatus(userId, status).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<PaymentOrder> findPendingOrders() {
        return jpaRepository.findPendingOrders().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByStatus(PaymentOrderStatus status) {
        return jpaRepository.countByStatus(status);
    }
}


