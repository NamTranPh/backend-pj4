package com.example.backend_pj4.application.usecase.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.payment.HandlePayOsWebhookUseCase;
import com.example.backend_pj4.application.port.out.PaymentGatewayPort;
import com.example.backend_pj4.application.port.out.PaymentGatewayPort.WebhookPayload;
import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HandlePayOsWebhookService implements HandlePayOsWebhookUseCase {

    private final PaymentGatewayPort paymentGatewayPort;
    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentSettlementService settlementService;

    public HandlePayOsWebhookService(
            PaymentGatewayPort paymentGatewayPort,
            PaymentOrderRepository paymentOrderRepository,
            PaymentSettlementService settlementService
    ) {
        this.paymentGatewayPort = paymentGatewayPort;
        this.paymentOrderRepository = paymentOrderRepository;
        this.settlementService = settlementService;
    }

    @Override
    @Transactional
    public void execute(String webhookBody) {
        WebhookPayload payload = paymentGatewayPort.verifyWebhookData(webhookBody);

        String orderCodeStr = String.valueOf(payload.orderCode());
        PaymentOrder order = paymentOrderRepository.findByTransactionCode(orderCodeStr)
                .orElse(null);

        if (order == null) {
            log.warn("PayOS webhook: order not found for orderCode={}", orderCodeStr);
            return;
        }

        if ("PAID".equals(payload.status())) {
            settlementService.completePayment(order, payload.transactionId());
        } else {
            settlementService.failPayment(order, "PayOS payment failed");
        }
    }
}
