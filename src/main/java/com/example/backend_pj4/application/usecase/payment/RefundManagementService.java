package com.example.backend_pj4.application.usecase.payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.payment.PaymentRefundResult;
import com.example.backend_pj4.application.mapper.PaymentRefundResultMapper;
import com.example.backend_pj4.application.port.in.payment.CancelRefundUseCase;
import com.example.backend_pj4.application.port.in.payment.CreateRefundUseCase;
import com.example.backend_pj4.application.port.in.payment.ProcessRefundUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.NotificationReferenceType;
import com.example.backend_pj4.common.constants.enums.NotificationType;
import com.example.backend_pj4.common.constants.enums.PaymentOrderStatus;
import com.example.backend_pj4.common.constants.enums.RefundStatus;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Notification;
import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.model.PaymentRefund;
import com.example.backend_pj4.domain.repository.MembershipRepository;
import com.example.backend_pj4.domain.repository.NotificationRepository;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;
import com.example.backend_pj4.domain.repository.PaymentRefundRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RefundManagementService implements CreateRefundUseCase, ProcessRefundUseCase, CancelRefundUseCase {

    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentRefundRepository paymentRefundRepository;
    private final MembershipRepository membershipRepository;
    private final NotificationRepository notificationRepository;

    public RefundManagementService(
            PaymentOrderRepository paymentOrderRepository,
            PaymentRefundRepository paymentRefundRepository,
            MembershipRepository membershipRepository,
            NotificationRepository notificationRepository
    ) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.paymentRefundRepository = paymentRefundRepository;
        this.membershipRepository = membershipRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public PaymentRefundResult execute(String paymentOrderId, BigDecimal amount, String reason, String adminId) {
        PaymentOrder order = paymentOrderRepository.findById(paymentOrderId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy đơn hàng: " + paymentOrderId));

        if (order.getStatus() != PaymentOrderStatus.COMPLETED && order.getStatus() != PaymentOrderStatus.REFUNDED) {
            throw new CustomException(ErrorCode.PAYMENT_ORDER_NOT_REFUNDABLE,
                    "Chỉ có thể tạo hoàn tiền cho đơn đã thanh toán thành công (trạng thái hiện tại: " + order.getStatus() + ")");
        }

        List<PaymentRefund> existingRefunds = paymentRefundRepository.findByPaymentOrderId(order.getId());
        BigDecimal totalActiveRefunds = existingRefunds.stream()
                .filter(r -> r.getStatus() == RefundStatus.COMPLETED
                        || r.getStatus() == RefundStatus.PROCESSING
                        || r.getStatus() == RefundStatus.REQUESTED)
                .map(PaymentRefund::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalActiveRefunds.add(amount).compareTo(order.getPrice()) > 0) {
            throw new CustomException(ErrorCode.BAD_REQUEST,
                    "Tổng số tiền hoàn (" + totalActiveRefunds.add(amount) + " đ) vượt quá giá trị đơn hàng (" + order.getPrice() + " đ)");
        }

        PaymentRefund refund = PaymentRefund.builder()
                .id(UUID.randomUUID().toString())
                .paymentOrderId(order.getId())
                .userId(order.getUserId())
                .amount(amount)
                .status(RefundStatus.REQUESTED)
                .reason(reason)
                .requestedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PaymentRefund saved = paymentRefundRepository.save(refund);
        log.info("Created refund request id={} for orderId={} amount={} by adminId={}",
                saved.getId(), order.getId(), amount, adminId);
        return PaymentRefundResultMapper.toResult(saved);
    }

    @Override
    @Transactional
    public PaymentRefundResult execute(String refundId, String refundTransactionCode, String adminNote, String adminId) {
        PaymentRefund refund = paymentRefundRepository.findById(refundId)
                .orElseThrow(() -> new CustomException(ErrorCode.REFUND_NOT_FOUND, "Không tìm thấy yêu cầu hoàn tiền: " + refundId));

        if (refund.getStatus() != RefundStatus.REQUESTED && refund.getStatus() != RefundStatus.PROCESSING) {
            throw new CustomException(ErrorCode.REFUND_CANNOT_BE_PROCESSED,
                    "Yêu cầu hoàn tiền đã ở trạng thái " + refund.getStatus() + ", không thể xử lý tiếp");
        }

        PaymentOrder order = paymentOrderRepository.findById(refund.getPaymentOrderId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy đơn hàng: " + refund.getPaymentOrderId()));

        // Cập nhật số tiền đã hoàn và trạng thái đơn hàng
        BigDecimal currentRefunded = order.getRefundedAmount() != null ? order.getRefundedAmount() : BigDecimal.ZERO;
        BigDecimal newRefunded = currentRefunded.add(refund.getAmount());

        PaymentOrder updatedOrder = order.toBuilder()
                .status(PaymentOrderStatus.REFUNDED)
                .refundedAt(LocalDateTime.now())
                .refundedAmount(newRefunded)
                .build();
        paymentOrderRepository.save(updatedOrder);

        // Thu hồi hoặc rút ngắn thời hạn Membership tương ứng
        membershipRepository.findActiveByUserId(order.getUserId()).ifPresent(membership -> {
            LocalDate newEndDate = membership.getEndDate().minusDays(order.getDurationDays());
            if (!newEndDate.isAfter(LocalDate.now())) {
                membership = membership.toBuilder()
                        .isActive(false)
                        .endDate(LocalDate.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
            } else {
                membership = membership.toBuilder()
                        .endDate(newEndDate)
                        .updatedAt(LocalDateTime.now())
                        .build();
            }
            membershipRepository.save(membership);
            log.info("Adjusted membership for userId={} newEndDate={} isActive={}",
                    membership.getUserId(), membership.getEndDate(), membership.getIsActive());
        });

        // Gửi thông báo đến người dùng
        notificationRepository.save(Notification.builder()
                .userId(order.getUserId())
                .type(NotificationType.SYSTEM)
                .title("Hoàn tiền thành công")
                .content("Yêu cầu hoàn tiền " + refund.getAmount() + " đ cho đơn hàng #" + order.getTransactionCode() + " đã được thực hiện thành công.")
                .referenceType(NotificationReferenceType.MEMBERSHIP)
                .referenceId(order.getMembershipPlanId())
                .isRead(false)
                .build());

        // Cập nhật phiếu hoàn tiền
        PaymentRefund processedRefund = refund.toBuilder()
                .status(RefundStatus.COMPLETED)
                .refundTransactionCode(refundTransactionCode)
                .adminNote(adminNote)
                .processedBy(adminId)
                .processedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PaymentRefund savedRefund = paymentRefundRepository.save(processedRefund);
        log.info("Processed refund id={} orderId={} txCode={} by adminId={}",
                savedRefund.getId(), order.getId(), refundTransactionCode, adminId);
        return PaymentRefundResultMapper.toResult(savedRefund);
    }

    @Override
    @Transactional
    public PaymentRefundResult execute(String refundId, String adminNote, String adminId) {
        PaymentRefund refund = paymentRefundRepository.findById(refundId)
                .orElseThrow(() -> new CustomException(ErrorCode.REFUND_NOT_FOUND, "Không tìm thấy yêu cầu hoàn tiền: " + refundId));

        if (refund.getStatus() == RefundStatus.COMPLETED) {
            throw new CustomException(ErrorCode.REFUND_CANNOT_BE_CANCELLED,
                    "Không thể hủy yêu cầu hoàn tiền đã hoàn tất");
        }

        if (refund.getStatus() == RefundStatus.CANCELLED || refund.getStatus() == RefundStatus.REJECTED) {
            return PaymentRefundResultMapper.toResult(refund);
        }

        PaymentRefund cancelledRefund = refund.toBuilder()
                .status(RefundStatus.CANCELLED)
                .adminNote(adminNote)
                .processedBy(adminId)
                .processedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PaymentRefund savedRefund = paymentRefundRepository.save(cancelledRefund);
        log.info("Cancelled refund id={} by adminId={}", savedRefund.getId(), adminId);
        return PaymentRefundResultMapper.toResult(savedRefund);
    }
}
