package com.example.backend_pj4.application.port.in.rating;

import com.example.backend_pj4.application.dto.rating.RatingSummaryResult;

/** Cổng ĐỌC điểm trung bình và số lượt đánh giá của một phim. */
public interface GetRatingSummaryQuery {
    RatingSummaryResult execute(String movieSlug);
}
