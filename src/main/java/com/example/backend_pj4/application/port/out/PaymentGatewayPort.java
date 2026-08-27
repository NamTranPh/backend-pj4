package com.example.backend_pj4.application.port.out;

import java.math.BigDecimal;

public interface PaymentGatewayPort {

    CreatePaymentLinkResult createPaymentLink(long orderCode, BigDecimal amount, String description,
                                               String returnUrl, String cancelUrl, Long expiredAt);

    WebhookPayload verifyWebhookData(String webhookBody);

    PaymentStatusResult getPaymentStatus(String paymentLinkId);

    void cancelPaymentLink(String paymentLinkId);

    record CreatePaymentLinkResult(String checkoutUrl, String paymentLinkId) {}

    record WebhookPayload(long orderCode, String status, String transactionId) {}

    record PaymentStatusResult(String status, long orderCode, BigDecimal amount) {}
}
