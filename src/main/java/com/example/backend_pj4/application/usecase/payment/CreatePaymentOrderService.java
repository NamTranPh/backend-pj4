package com.example.backend_pj4.application.usecase.payment;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.payment.PaymentOrderResult;
import com.example.backend_pj4.application.mapper.PaymentResultMapper;
import com.example.backend_pj4.application.port.in.payment.CreatePaymentOrderUseCase;
import com.example.backend_pj4.application.port.out.PaymentGatewayPort;
import com.example.backend_pj4.application.port.out.PaymentGatewayPort.CreatePaymentLinkResult;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.PaymentOrderStatus;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.MembershipPlan;
import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.repository.MembershipPlanRepository;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;
import com.example.backend_pj4.infrastructure.config.properties.PayOsProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CreatePaymentOrderService implements CreatePaymentOrderUseCase {

    private final MembershipPlanRepository membershipPlanRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentGatewayPort paymentGatewayPort;
    private final PayOsProperties payOsProperties;

    /**
     * Bộ sinh mã đơn tăng dần
     */
    private final AtomicLong orderCodeSequence = new AtomicLong(0);

    public CreatePaymentOrderService(
            MembershipPlanRepository membershipPlanRepository,
            PaymentOrderRepository paymentOrderRepository,
            PaymentGatewayPort paymentGatewayPort,
            PayOsProperties payOsProperties
    ) {
        this.membershipPlanRepository = membershipPlanRepository;
        this.paymentOrderRepository = paymentOrderRepository;
        this.paymentGatewayPort = paymentGatewayPort;
        this.payOsProperties = payOsProperties;
    }

    @Override
    public PaymentOrderResult execute(String userId, String planSlug) {
        MembershipPlan plan = membershipPlanRepository.findBySlug(planSlug)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBERSHIP_PLAN_NOT_FOUND));

        if (!Boolean.TRUE.equals(plan.getIsActive())) {
            throw new CustomException(ErrorCode.MEMBERSHIP_PLAN_NOT_FOUND);
        }

        long orderCode = nextOrderCode();

        PaymentOrder order = PaymentOrder.builder()
                .userId(userId)
                .membershipPlanId(plan.getId())
                .planName(plan.getName())
                .price(plan.getPrice())
                .durationDays(plan.getDurationDays())
                .gateway("PAYOS")
                .status(PaymentOrderStatus.PENDING)
                .transactionCode(String.valueOf(orderCode))
                .requestedAt(LocalDateTime.now())
                .build();

        // Bước 1: Lưu đơn PENDING vào DB (tự commit và giải phóng DB connection ngay lập tức)
        order = paymentOrderRepository.save(order);

        String description = "Mua goi " + plan.getName();
        if (description.length() > 25) {
            description = description.substring(0, 25);
        }

        long expiredAt = Instant.now().getEpochSecond() + (long) payOsProperties.getPaymentLinkTtlMinutes() * 60;

        // Bước 2: Gọi HTTP ra cổng PayOS (Hoàn toàn nằm ngoài DB transaction)
        CreatePaymentLinkResult linkResult;
        try {
            linkResult = paymentGatewayPort.createPaymentLink(
                    orderCode,
                    plan.getPrice(),
                    description,
                    payOsProperties.getReturnUrl(),
                    payOsProperties.getCancelUrl(),
                    expiredAt
            );
        } catch (Exception e) {
            log.error("PayOS createPaymentLink thất bại cho orderId={}, code={}", order.getId(), orderCode, e);
            paymentOrderRepository.markFailedIfPending(
                    order.getId(),
                    "Không thể tạo link thanh toán PayOS",
                    LocalDateTime.now()
            );
            throw new CustomException(ErrorCode.PAYMENT_CREATION_FAILED);
        }

        // Bước 3: Cập nhật checkoutUrl và gatewayTransactionId vào DB
        order = order.toBuilder()
                .checkoutUrl(linkResult.checkoutUrl())
                .gatewayTransactionId(linkResult.paymentLinkId())
                .build();
        order = paymentOrderRepository.save(order);

        return PaymentResultMapper.toResult(order);
    }

    private long nextOrderCode() {
        long now = System.currentTimeMillis();
        return orderCodeSequence.updateAndGet(previous -> Math.max(previous + 1, now));
    }
}
