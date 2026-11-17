package com.example.backend_pj4.application.usecase.payment;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.payment.PaymentOrderResult;
import com.example.backend_pj4.application.mapper.PaymentResultMapper;
import com.example.backend_pj4.application.port.in.payment.SyncPaymentAdminUseCase;
import com.example.backend_pj4.application.port.out.PaymentGatewayPort;
import com.example.backend_pj4.application.port.out.PaymentGatewayPort.PaymentStatusResult;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SyncPaymentAdminService implements SyncPaymentAdminUseCase {

    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentGatewayPort paymentGatewayPort;
    private final PaymentSettlementService settlementService;

    public SyncPaymentAdminService(
            PaymentOrderRepository paymentOrderRepository,
            PaymentGatewayPort paymentGatewayPort,
            PaymentSettlementService settlementService
    ) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.paymentGatewayPort = paymentGatewayPort;
        this.settlementService = settlementService;
    }

    @Override
    public PaymentOrderResult execute(String orderId) {
        PaymentOrder order = paymentOrderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        if (order.getGatewayTransactionId() == null) {
            log.warn("Đơn hàng #{} chưa có mã giao dịch từ cổng, không thể đồng bộ.", order.getId());
            return PaymentResultMapper.toResult(order);
        }

        PaymentStatusResult statusResult = paymentGatewayPort.getPaymentStatus(order.getGatewayTransactionId());
        log.info("Đồng bộ PayOS orderId={}, status={}", orderId, statusResult.status());

        switch (statusResult.status()) {
            case "PAID" -> settlementService.completePayment(order, order.getGatewayTransactionId(), statusResult.amount());
            case "EXPIRED", "CANCELLED" -> settlementService.cancelPayment(order, "PayOS status: " + statusResult.status());
            case "FAILED" -> settlementService.failPayment(order, "PayOS status: " + statusResult.status());
            default -> { /* Vẫn PENDING, không làm gì */ }
        }

        order = paymentOrderRepository.findById(orderId).orElse(order);
        return PaymentResultMapper.toResult(order);
    }
}
