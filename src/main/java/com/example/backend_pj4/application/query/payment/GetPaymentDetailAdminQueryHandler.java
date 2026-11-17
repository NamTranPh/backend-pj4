package com.example.backend_pj4.application.query.payment;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.payment.AdminPaymentDetailResult;
import com.example.backend_pj4.application.dto.payment.PaymentRefundResult;
import com.example.backend_pj4.application.mapper.PaymentRefundResultMapper;
import com.example.backend_pj4.application.port.in.payment.GetPaymentDetailAdminQuery;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.MembershipPlan;
import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.MembershipPlanRepository;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;
import com.example.backend_pj4.domain.repository.PaymentRefundRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class GetPaymentDetailAdminQueryHandler implements GetPaymentDetailAdminQuery {

    private final PaymentOrderRepository paymentOrderRepository;
    private final UserRepository userRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final PaymentRefundRepository paymentRefundRepository;

    public GetPaymentDetailAdminQueryHandler(
            PaymentOrderRepository paymentOrderRepository,
            UserRepository userRepository,
            MembershipPlanRepository membershipPlanRepository,
            PaymentRefundRepository paymentRefundRepository
    ) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.userRepository = userRepository;
        this.membershipPlanRepository = membershipPlanRepository;
        this.paymentRefundRepository = paymentRefundRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminPaymentDetailResult execute(String orderId) {
        PaymentOrder order = paymentOrderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        AdminPaymentDetailResult.UserInfo userInfo = null;
        if (order.getUserId() != null) {
            User user = userRepository.findById(order.getUserId()).orElse(null);
            if (user != null) {
                userInfo = new AdminPaymentDetailResult.UserInfo(
                        user.getId(),
                        user.getEmail(),
                        user.getName(),
                        user.getProfileUrl()
                );
            }
        }

        AdminPaymentDetailResult.PlanInfo planInfo = null;
        if (order.getMembershipPlanId() != null) {
            MembershipPlan plan = membershipPlanRepository.findById(order.getMembershipPlanId()).orElse(null);
            if (plan != null) {
                planInfo = new AdminPaymentDetailResult.PlanInfo(
                        plan.getId(),
                        plan.getName(),
                        plan.getDurationDays(),
                        plan.getPrice()
                );
            }
        }

        List<PaymentRefundResult> refundHistory = paymentRefundRepository.findByPaymentOrderId(order.getId())
                .stream()
                .map(PaymentRefundResultMapper::toResult)
                .toList();

        return new AdminPaymentDetailResult(
                order.getId(),
                userInfo,
                planInfo,
                order.getPrice(),
                order.getDurationDays(),
                order.getGateway(),
                order.getStatus() != null ? order.getStatus().name() : null,
                order.getTransactionCode(),
                order.getGatewayTransactionId(),
                order.getCheckoutUrl(),
                order.getFailureReason(),
                order.getMetadata(),
                order.getRequestedAt(),
                order.getCompletedAt(),
                order.getFailedAt(),
                order.getCancelledAt(),
                order.getRefundedAt(),
                order.getRefundedAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                refundHistory
        );
    }
}
