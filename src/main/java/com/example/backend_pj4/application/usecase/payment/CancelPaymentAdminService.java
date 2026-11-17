package com.example.backend_pj4.application.usecase.payment;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.payment.PaymentOrderResult;
import com.example.backend_pj4.application.mapper.PaymentResultMapper;
import com.example.backend_pj4.application.port.in.payment.CancelPaymentAdminUseCase;
import com.example.backend_pj4.application.port.out.PaymentGatewayPort;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.PaymentOrderStatus;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CancelPaymentAdminService implements CancelPaymentAdminUseCase {

    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentSettlementService settlementService;
    private final PaymentGatewayPort paymentGatewayPort;

    public CancelPaymentAdminService(
            PaymentOrderRepository paymentOrderRepository,
            PaymentSettlementService settlementService,
            PaymentGatewayPort paymentGatewayPort
    ) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.settlementService = settlementService;
        this.paymentGatewayPort = paymentGatewayPort;
    }

    @Override
    public PaymentOrderResult execute(String orderId, String reason) {
        PaymentOrder order = paymentOrderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        if (order.getStatus() != PaymentOrderStatus.PENDING) {
            log.info("Admin hủy đơn #{}, nhưng đơn đang ở trạng thái: {}", orderId, order.getStatus());
            return PaymentResultMapper.toResult(order);
        }

        boolean cancelled = settlementService.cancelPayment(order, reason);
        if (!cancelled) {
            order = paymentOrderRepository.findById(orderId).orElse(order);
            return PaymentResultMapper.toResult(order);
        }

        if (order.getGatewayTransactionId() != null) {
            try {
                paymentGatewayPort.cancelPaymentLink(order.getGatewayTransactionId());
            } catch (Exception e) {
                log.warn("Lỗi khi gọi hủy link PayOS cho orderId={}", orderId, e);
            }
        }

        order = order.toBuilder()
                .status(PaymentOrderStatus.CANCELLED)
                .cancelledAt(LocalDateTime.now())
                .failureReason(reason)
                .build();

        return PaymentResultMapper.toResult(order);
    }
}
