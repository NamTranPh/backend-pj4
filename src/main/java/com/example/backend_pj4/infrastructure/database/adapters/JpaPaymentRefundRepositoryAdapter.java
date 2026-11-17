package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.PaymentRefund;
import com.example.backend_pj4.domain.repository.PaymentRefundRepository;
import com.example.backend_pj4.domain.repository.filter.PaymentRefundFilter;
import com.example.backend_pj4.infrastructure.database.mappers.PaymentRefundPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.PaymentRefundJpaRepository;
import com.example.backend_pj4.infrastructure.database.specifications.PaymentRefundSpecification;

@Repository
public class JpaPaymentRefundRepositoryAdapter implements PaymentRefundRepository {

    private final PaymentRefundJpaRepository jpaRepository;
    private final PaymentRefundPersistenceMapper mapper;

    public JpaPaymentRefundRepositoryAdapter(PaymentRefundJpaRepository jpaRepository,
                                             PaymentRefundPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public PaymentRefund save(PaymentRefund refund) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(refund)));
    }

    @Override
    public Optional<PaymentRefund> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<PaymentRefund> findByPaymentOrderId(String paymentOrderId) {
        return jpaRepository.findByPaymentOrder_IdOrderByCreatedAtDesc(paymentOrderId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public PageResult<PaymentRefund> findWithFilter(PaymentRefundFilter filter, PageQuery query) {
        var spec = PaymentRefundSpecification.buildFilter(filter);
        var pageable = PageableMapper.toPageable(query);
        var page = jpaRepository.findAll(spec, pageable);
        return PageableMapper.toPageResult(page, mapper::toDomain);
    }
}
