package com.example.backend_pj4.application.dto.rating;

import java.math.BigDecimal;

/**
 * Tóm tắt đánh giá của một phim.
 */
public record RatingSummaryResult(
        BigDecimal averageScore,
        long totalRatings) {
}
