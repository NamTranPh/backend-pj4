package com.example.backend_pj4.application.usecase.payment;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.common.constants.enums.MembershipPaymentStatus;
import com.example.backend_pj4.common.constants.enums.NotificationReferenceType;
import com.example.backend_pj4.common.constants.enums.NotificationType;
import com.example.backend_pj4.common.constants.enums.PaymentOrderStatus;
import com.example.backend_pj4.domain.model.Membership;
import com.example.backend_pj4.domain.model.Notification;
import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.repository.MembershipRepository;
import com.example.backend_pj4.domain.repository.NotificationRepository;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentSettlementService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final MembershipRepository membershipRepository;
    private final NotificationRepository notificationRepository;

    public PaymentSettlementService(
            PaymentOrderRepository paymentOrderRepository,
            MembershipRepository membershipRepository,
            NotificationRepository notificationRepository
    ) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.membershipRepository = membershipRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public boolean completePayment(PaymentOrder order, String gatewayTransactionId) {
        if (order.getStatus() == PaymentOrderStatus.COMPLETED) {
            log.info("Order already completed orderId={}", order.getId());
            return false;
        }

        order = order.toBuilder()
                .status(PaymentOrderStatus.COMPLETED)
                .completedAt(LocalDateTime.now())
                .gatewayTransactionId(gatewayTransactionId != null ? gatewayTransactionId : order.getGatewayTransactionId())
                .build();
        paymentOrderRepository.save(order);

        activateMembership(order);

        notificationRepository.save(Notification.builder()
                .userId(order.getUserId())
                .type(NotificationType.SYSTEM)
                .title("Thanh toán thành công")
                .content("Đã kích hoạt gói " + order.getPlanName() + " thành công.")
                .referenceType(NotificationReferenceType.MEMBERSHIP)
                .referenceId(order.getMembershipPlanId())
                .isRead(false)
                .build());

        log.info("Payment completed orderId={} transactionCode={}", order.getId(), order.getTransactionCode());
        return true;
    }

    @Transactional
    public boolean failPayment(PaymentOrder order, String reason) {
        if (order.getStatus() != PaymentOrderStatus.PENDING) {
            return false;
        }

        order = order.toBuilder()
                .status(PaymentOrderStatus.FAILED)
                .failedAt(LocalDateTime.now())
                .failureReason(reason)
                .build();
        paymentOrderRepository.save(order);

        log.info("Payment failed orderId={} reason={}", order.getId(), reason);
        return true;
    }

    private void activateMembership(PaymentOrder order) {
        var existingMembership = membershipRepository.findActiveByUserId(order.getUserId());

        LocalDate startDate = LocalDate.now();
        LocalDate endDate;

        if (existingMembership.isPresent()) {
            Membership existing = existingMembership.get();
            if (existing.getEndDate().isAfter(startDate)) {
                startDate = existing.getEndDate();
            }
            endDate = startDate.plusDays(order.getDurationDays());

            Membership extended = existing.toBuilder()
                    .endDate(endDate)
                    .updatedAt(LocalDateTime.now())
                    .build();
            membershipRepository.save(extended);
        } else {
            endDate = startDate.plusDays(order.getDurationDays());
            Membership newMembership = Membership.builder()
                    .userId(order.getUserId())
                    .membershipPlanId(order.getMembershipPlanId())
                    .startDate(startDate)
                    .endDate(endDate)
                    .paymentStatus(MembershipPaymentStatus.PAID)
                    .autoRenewal(false)
                    .isActive(true)
                    .build();
            membershipRepository.save(newMembership);
        }
    }
}
