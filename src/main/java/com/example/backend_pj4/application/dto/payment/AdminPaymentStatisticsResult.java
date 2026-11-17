package com.example.backend_pj4.application.dto.payment;

import java.math.BigDecimal;
import java.util.List;

public record AdminPaymentStatisticsResult(
        BigDecimal grossRevenue,
        BigDecimal totalRefunded,
        BigDecimal netRevenue,
        long totalOrders,
        long completedOrders,
        long pendingOrders,
        long cancelledOrders,
        long failedOrders,
        long refundedOrders,
        double successRate,
        List<PlanRevenueItem> revenueByPlan
) {
    public record PlanRevenueItem(String planId, String planName, BigDecimal revenue, long orderCount) {}
}
