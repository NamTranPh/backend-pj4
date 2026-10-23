package com.example.backend_pj4.application.query.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.payment.PaymentOrderResult;
import com.example.backend_pj4.application.port.in.payment.ListMyPaymentOrdersQuery;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;

@Service
public class ListMyPaymentOrdersQueryHandler implements ListMyPaymentOrdersQuery {

    private final PaymentOrderRepository paymentOrderRepository;

    public ListMyPaymentOrdersQueryHandler(PaymentOrderRepository paymentOrderRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<PaymentOrderResult> execute(String userId, PageQuery query) {
        return paymentOrderRepository.findByUserId(userId, query).map(this::toResult);
    }

    private PaymentOrderResult toResult(PaymentOrder order) {
        return new PaymentOrderResult(
                order.getId(),
                order.getPlanName(),
                order.getPrice(),
                order.getStatus() != null ? order.getStatus().name() : null,
                order.getTransactionCode(),
                order.getCheckoutUrl(),
                order.getCreatedAt(),
                order.getCompletedAt()
        );
    }
}
