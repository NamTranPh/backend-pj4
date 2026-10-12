package com.example.backend_pj4.application.usecase.payment;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

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

    /**
     * Bộ sinh mã đơn tăng dần nghiêm ngặt.
     *
     * <p>Trước đây mã sinh bằng {@code System.currentTimeMillis() % 1_000_000_000L}. Phép chia
     * lấy dư chỉ giữ 9 chữ số cuối, mà 9 chữ số đếm được tối đa 10⁹ mili-giây = <b>11,57 ngày</b>
     * — nên cứ 11,57 ngày mã lại lặp lại y hệt. Vì {@code transaction_code} có ràng buộc UNIQUE,
     * lần trùng đó làm INSERT ném lỗi và khách nhận 500 khi bấm mua gói. Đây không phải race
     * condition mà là phép toán, chắc chắn xảy ra sau khi chạy đủ lâu.
     *
     * <p>Bỏ phép {@code %} là xử lý xong việc lặp. {@code AtomicLong} xử lý nốt trường hợp hai
     * người bấm mua trong cùng một mili-giây: mã luôn lớn hơn mã trước ít nhất 1.
     *
     * <p>Giới hạn: chỉ đảm bảo duy nhất trong phạm vi một tiến trình. Khi chạy nhiều instance
     * thì cần chuyển sang sequence của database.
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
    @Transactional
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

    private long nextOrderCode() {
        long now = System.currentTimeMillis();
        return orderCodeSequence.updateAndGet(previous -> Math.max(previous + 1, now));
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
