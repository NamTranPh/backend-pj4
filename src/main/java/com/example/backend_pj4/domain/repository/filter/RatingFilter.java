package com.example.backend_pj4.domain.repository.filter;

/**
 * Bộ lọc danh sách đánh giá.
 *
 * @param movieId phim được đánh giá; null nghĩa là không lọc theo phim
 * @param userId  người đánh giá; null nghĩa là không lọc theo người
 */
public record RatingFilter(String movieId, String userId) {
}
