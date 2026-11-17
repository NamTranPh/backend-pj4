package com.example.backend_pj4.application.usecase.payment;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.payment.PaymentOrderResult;
import com.example.backend_pj4.application.mapper.PaymentResultMapper;
import com.example.backend_pj4.application.port.in.payment.CancelMyPaymentOrderUseCase;
import com.example.backend_pj4.application.port.out.PaymentGatewayPort;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.PaymentOrderStatus;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CancelMyPaymentOrderService implements CancelMyPaymentOrderUseCase {

    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentSettlementService settlementService;
    private final PaymentGatewayPort paymentGatewayPort;

    public CancelMyPaymentOrderService(
            PaymentOrderRepository paymentOrderRepository,
            PaymentSettlementService settlementService,
            PaymentGatewayPort paymentGatewayPort
    ) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.settlementService = settlementService;
        this.paymentGatewayPort = paymentGatewayPort;
    }

    @Override
    public PaymentOrderResult execute(String transactionCode, String userId) {
        PaymentOrder order = paymentOrderRepository.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        if (!order.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        // Nếu đơn không còn ở PENDING (ví dụ đã COMPLETED hoặc đã CANCELLED từ trước),
        // theo quy chuẩn Idempotent ta trả về luôn trạng thái hiện tại (HTTP 200) thay vì ném lỗi 400 làm đỏ F12.
        if (order.getStatus() != PaymentOrderStatus.PENDING) {
            log.info("Đơn hàng #{} không ở trạng thái PENDING (hiện tại: {}), trả về trạng thái hiện tại.",
                    transactionCode, order.getStatus());
            return PaymentResultMapper.toResult(order);
        }

        // Hủy nguyên tử trong DB
        boolean cancelled = settlementService.cancelPayment(order, "Người dùng chủ động hủy đơn");
        if (!cancelled) {
            // Trường hợp luồng khác (webhook hoặc job) vừa chuyển trạng thái đúng lúc bấm hủy
            order = paymentOrderRepository.findByTransactionCode(transactionCode).orElse(order);
            return PaymentResultMapper.toResult(order);
        }

        // Hủy link trên PayOS nếu đã có gatewayTransactionId
        if (order.getGatewayTransactionId() != null) {
            try {
                paymentGatewayPort.cancelPaymentLink(order.getGatewayTransactionId());
            } catch (Exception e) {
                log.warn("Lỗi hủy link PayOS cho orderId={}", order.getId(), e);
            }
        }

        order = order.toBuilder()
                .status(PaymentOrderStatus.CANCELLED)
                .cancelledAt(LocalDateTime.now())
                .failureReason("Người dùng chủ động hủy đơn")
                .build();

        return PaymentResultMapper.toResult(order);
    }
}
