package com.example.backend_pj4.application.usecase.payment;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.payment.PaymentOrderResult;
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

@Service
public class CreatePaymentOrderService implements CreatePaymentOrderUseCase {

    private final MembershipPlanRepository membershipPlanRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentGatewayPort paymentGatewayPort;
    private final PayOsProperties payOsProperties;

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
    @Transactional
    public PaymentOrderResult execute(String userId, String planSlug) {
        MembershipPlan plan = membershipPlanRepository.findBySlug(planSlug)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBERSHIP_PLAN_NOT_FOUND));

        if (!Boolean.TRUE.equals(plan.getIsActive())) {
            throw new CustomException(ErrorCode.MEMBERSHIP_PLAN_NOT_FOUND);
        }

        long orderCode = System.currentTimeMillis() % 1_000_000_000L;

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

        order = paymentOrderRepository.save(order);

        String description = "Mua goi " + plan.getName();
        if (description.length() > 25) {
            description = description.substring(0, 25);
        }

        long expiredAt = Instant.now().getEpochSecond() + (long) payOsProperties.getPaymentLinkTtlMinutes() * 60;

        CreatePaymentLinkResult linkResult = paymentGatewayPort.createPaymentLink(
                orderCode,
                plan.getPrice(),
                description,
                payOsProperties.getReturnUrl(),
                payOsProperties.getCancelUrl(),
                expiredAt
        );

        order = order.toBuilder()
                .checkoutUrl(linkResult.checkoutUrl())
                .gatewayTransactionId(linkResult.paymentLinkId())
                .build();
        order = paymentOrderRepository.save(order);

        return toResult(order);
    }

    private PaymentOrderResult toResult(PaymentOrder order) {
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
