package com.example.backend_pj4.infrastructure.database.adapters;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;
import com.example.backend_pj4.domain.repository.filter.PaymentOrderFilter;
import com.example.backend_pj4.infrastructure.database.mappers.PaymentOrderPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.PaymentOrderJpaRepository;
import com.example.backend_pj4.infrastructure.database.specifications.PaymentOrderSpecification;

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
    public PageResult<PaymentOrder> findByUserId(String userId, PageQuery query) {
        return PageableMapper.toPageResult(
                jpaRepository.findByUser_Id(userId, PageableMapper.toPageable(query)),
                mapper::toDomain);
    }

    @Override
    public List<PaymentOrder> findPendingByGatewaySince(String gateway, LocalDateTime since, int limit) {
        return jpaRepository.findPendingByGatewaySince(gateway, since, PageRequest.of(0, limit)).stream()
                .map(mapper::toDomain)
                .toList();
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
    @Transactional
    public int markCancelledIfPending(String orderId, String reason, LocalDateTime now) {
        return jpaRepository.markCancelledIfPending(orderId, reason, now);
    }

    @Override
    public PageResult<PaymentOrder> findWithFilter(PaymentOrderFilter filter, PageQuery query) {
        var spec = PaymentOrderSpecification.buildFilter(filter);
        var pageable = PageableMapper.toPageable(query);
        var page = jpaRepository.findAll(spec, pageable);
        return PageableMapper.toPageResult(page, mapper::toDomain);
    }

    @Override
    public List<PaymentOrder> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to) {
        var filter = new PaymentOrderFilter(null, null, null, null,
                from != null ? from.toLocalDate() : null,
                to != null ? to.toLocalDate() : null,
                null);
        var spec = PaymentOrderSpecification.buildFilter(filter);
        return jpaRepository.findAll(spec).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }
}
