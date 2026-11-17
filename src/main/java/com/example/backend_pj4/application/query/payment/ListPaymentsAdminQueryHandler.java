package com.example.backend_pj4.application.query.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.payment.PaymentOrderResult;
import com.example.backend_pj4.application.mapper.PaymentResultMapper;
import com.example.backend_pj4.application.port.in.payment.ListPaymentsAdminQuery;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;
import com.example.backend_pj4.domain.repository.filter.PaymentOrderFilter;

@Service
public class ListPaymentsAdminQueryHandler implements ListPaymentsAdminQuery {

    private final PaymentOrderRepository paymentOrderRepository;

    public ListPaymentsAdminQueryHandler(PaymentOrderRepository paymentOrderRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<PaymentOrderResult> execute(PaymentOrderFilter filter, PageQuery query) {
        return paymentOrderRepository.findWithFilter(filter, query).map(PaymentResultMapper::toResult);
    }
}
