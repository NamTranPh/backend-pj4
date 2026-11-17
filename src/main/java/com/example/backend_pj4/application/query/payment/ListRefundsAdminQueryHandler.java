package com.example.backend_pj4.application.query.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.payment.PaymentRefundResult;
import com.example.backend_pj4.application.mapper.PaymentRefundResultMapper;
import com.example.backend_pj4.application.port.in.payment.ListRefundsAdminQuery;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.PaymentRefundRepository;
import com.example.backend_pj4.domain.repository.filter.PaymentRefundFilter;

@Service
public class ListRefundsAdminQueryHandler implements ListRefundsAdminQuery {

    private final PaymentRefundRepository paymentRefundRepository;

    public ListRefundsAdminQueryHandler(PaymentRefundRepository paymentRefundRepository) {
        this.paymentRefundRepository = paymentRefundRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<PaymentRefundResult> execute(PaymentRefundFilter filter, PageQuery query) {
        return paymentRefundRepository.findWithFilter(filter, query).map(PaymentRefundResultMapper::toResult);
    }
}
