package com.example.backend_pj4.application.query.payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.payment.AdminPaymentStatisticsResult;
import com.example.backend_pj4.application.dto.payment.AdminPaymentStatisticsResult.PlanRevenueItem;
import com.example.backend_pj4.application.port.in.payment.GetPaymentStatisticsAdminQuery;
import com.example.backend_pj4.common.constants.enums.PaymentOrderStatus;
import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;

@Service
public class GetPaymentStatisticsAdminQueryHandler implements GetPaymentStatisticsAdminQuery {

    private final PaymentOrderRepository paymentOrderRepository;

    public GetPaymentStatisticsAdminQueryHandler(PaymentOrderRepository paymentOrderRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminPaymentStatisticsResult execute(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime start = fromDate != null ? fromDate.atStartOfDay() : LocalDateTime.now().minusDays(30).with(LocalTime.MIN);
        LocalDateTime end = toDate != null ? toDate.atTime(LocalTime.MAX) : LocalDateTime.now().with(LocalTime.MAX);

        List<PaymentOrder> orders = paymentOrderRepository.findAllByCreatedAtBetween(start, end);

        long totalOrders = orders.size();
        long completedOrders = 0;
        long pendingOrders = 0;
        long cancelledOrders = 0;
        long failedOrders = 0;
        long refundedOrders = 0;

        BigDecimal grossRevenue = BigDecimal.ZERO;
        BigDecimal totalRefunded = BigDecimal.ZERO;

        Map<String, PlanStatAccumulator> planStats = new HashMap<>();

        for (PaymentOrder o : orders) {
            PaymentOrderStatus st = o.getStatus();
            if (st == null) continue;

            switch (st) {
                case COMPLETED -> {
                    completedOrders++;
                    if (o.getPrice() != null) {
                        grossRevenue = grossRevenue.add(o.getPrice());
                    }
                    accumulatePlan(planStats, o);
                }
                case PENDING -> pendingOrders++;
                case CANCELLED -> cancelledOrders++;
                case FAILED -> failedOrders++;
                case REFUNDED -> {
                    refundedOrders++;
                    if (o.getPrice() != null) {
                        grossRevenue = grossRevenue.add(o.getPrice());
                    }
                    if (o.getRefundedAmount() != null) {
                        totalRefunded = totalRefunded.add(o.getRefundedAmount());
                    }
                    accumulatePlan(planStats, o);
                }
            }
        }

        BigDecimal netRevenue = grossRevenue.subtract(totalRefunded);
        double successRate = totalOrders > 0
                ? ((double) (completedOrders + refundedOrders) / totalOrders) * 100.0
                : 0.0;

        List<PlanRevenueItem> revenueByPlan = new ArrayList<>();
        for (PlanStatAccumulator acc : planStats.values()) {
            revenueByPlan.add(new PlanRevenueItem(acc.planId, acc.planName, acc.revenue, acc.count));
        }

        return new AdminPaymentStatisticsResult(
                grossRevenue,
                totalRefunded,
                netRevenue,
                totalOrders,
                completedOrders,
                pendingOrders,
                cancelledOrders,
                failedOrders,
                refundedOrders,
                Math.round(successRate * 10.0) / 10.0,
                revenueByPlan
        );
    }

    private void accumulatePlan(Map<String, PlanStatAccumulator> map, PaymentOrder o) {
        String key = o.getMembershipPlanId() != null ? o.getMembershipPlanId() : o.getPlanName();
        if (key == null) return;

        PlanStatAccumulator acc = map.computeIfAbsent(key, k -> new PlanStatAccumulator(o.getMembershipPlanId(), o.getPlanName()));
        acc.count++;
        if (o.getPrice() != null) {
            acc.revenue = acc.revenue.add(o.getPrice());
        }
    }

    private static class PlanStatAccumulator {
        String planId;
        String planName;
        BigDecimal revenue = BigDecimal.ZERO;
        long count = 0;

        PlanStatAccumulator(String planId, String planName) {
            this.planId = planId;
            this.planName = planName;
        }
    }
}
