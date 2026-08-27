// Payment reconciliation job — job đối soát thanh toán: 
// để tránh bị miss/trạng thái thanh toán không được cập nhật, đặc biệt khi webhook từ PayOS bị lỗi, không tới, hoặc hệ thống gặp sự cố.
package com.example.backend_pj4.infrastructure.jobs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.port.out.PaymentGatewayPort;
import com.example.backend_pj4.application.port.out.PaymentGatewayPort.PaymentStatusResult;
import com.example.backend_pj4.application.usecase.payment.PaymentSettlementService;
import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;
import com.example.backend_pj4.infrastructure.config.properties.PayOsProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PaymentReconcileJob {

    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentGatewayPort paymentGatewayPort;
    private final PaymentSettlementService settlementService;
    private final PayOsProperties payOsProperties;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public PaymentReconcileJob(
            PaymentOrderRepository paymentOrderRepository,
            PaymentGatewayPort paymentGatewayPort,
            PaymentSettlementService settlementService,
            PayOsProperties payOsProperties
    ) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.paymentGatewayPort = paymentGatewayPort;
        this.settlementService = settlementService;
        this.payOsProperties = payOsProperties;
    }

    @Scheduled(fixedRateString = "${app.payos.reconcile-interval-ms:300000}")
    public void reconcilePendingPayments() {
        if (!running.compareAndSet(false, true)) {
            log.debug("Reconcile job already running, skipping");
            return;
        }

        try {
            LocalDateTime since = LocalDateTime.now().minusHours(payOsProperties.getReconcileLookbackHours());
            List<PaymentOrder> pendingOrders = paymentOrderRepository.findPendingByGatewaySince(
                    "PAYOS", since, payOsProperties.getReconcileBatchLimit());

            if (pendingOrders.isEmpty()) {
                return;
            }

            log.info("Reconcile: scanning {} pending PayOS orders", pendingOrders.size());

            int completed = 0;
            int expired = 0;
            int failed = 0;
            int errors = 0;

            for (PaymentOrder order : pendingOrders) {
                try {
                    boolean isExpired = isPaymentLinkExpired(order);
                    if (isExpired) {
                        cancelAndFail(order);
                        expired++;
                        continue;
                    }

                    if (order.getGatewayTransactionId() == null) {
                        continue;
                    }

                    PaymentStatusResult status = paymentGatewayPort.getPaymentStatus(order.getGatewayTransactionId());

                    // Kiểm tra giá trị từ PayOs trả về
                    switch (status.status()) {
                        case "PAID" -> {
                            settlementService.completePayment(order, order.getGatewayTransactionId());
                            completed++;
                        }
                        case "EXPIRED", "CANCELLED" -> {
                            settlementService.failPayment(order, "PayOS status: " + status.status());
                            failed++;
                        }
                        default -> { /* still PENDING, skip */ }
                    }
                } catch (Exception e) {
                    log.warn("Reconcile: error processing orderId={}", order.getId(), e);
                    errors++;
                }
            }

            log.info("Reconcile done: scanned={} completed={} expired={} failed={} errors={}",
                    pendingOrders.size(), completed, expired, failed, errors);
        } finally {
            running.set(false);
        }
    }

    private boolean isPaymentLinkExpired(PaymentOrder order) {
        if (order.getCreatedAt() == null) {
            return false;
        }
        LocalDateTime expiryTime = order.getCreatedAt().plusMinutes(payOsProperties.getPaymentLinkTtlMinutes());
        return LocalDateTime.now().isAfter(expiryTime);
    }

    private void cancelAndFail(PaymentOrder order) {
        if (order.getGatewayTransactionId() != null) {
            try {
                paymentGatewayPort.cancelPaymentLink(order.getGatewayTransactionId());
            } catch (Exception e) {
                log.warn("Reconcile: cancel link failed for orderId={}", order.getId(), e);
            }
        }
        settlementService.failPayment(order, "Payment link expired");
    }
}
