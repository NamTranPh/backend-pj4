package com.example.backend_pj4.application.port.out;

import java.util.Optional;

import com.example.backend_pj4.application.dto.rating.RatingSummaryResult;

/**
 * Cache điểm trung bình của phim. Mỗi phim đúng một khoá nên xoá thẳng là đủ, không cần version.
 *
 * <p>Hỏng cache không được làm hỏng nghiệp vụ (AGENTS.md §18).
 */
public interface RatingSummaryCache {

    Optional<RatingSummaryResult> get(String movieId);

    void put(String movieId, RatingSummaryResult value);

    void evict(String movieId);
}
