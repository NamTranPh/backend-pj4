package com.example.backend_pj4.application.usecase.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.payment.PaymentOrderResult;
import com.example.backend_pj4.application.port.in.payment.GetPaymentStatusUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;

@Service
public class GetPaymentStatusService implements GetPaymentStatusUseCase {

    private final PaymentOrderRepository paymentOrderRepository;

    public GetPaymentStatusService(PaymentOrderRepository paymentOrderRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentOrderResult execute(String transactionCode, String userId) {
        PaymentOrder order = paymentOrderRepository.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        if (!order.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        return new PaymentOrderResult(
                order.getId(),
                order.getPlanName(),
                order.getPrice(),
                order.getStatus().name(),
                order.getTransactionCode(),
                order.getCheckoutUrl(),
                order.getCreatedAt(),
                order.getCompletedAt()
        );
    }
}
