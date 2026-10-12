package com.example.backend_pj4.domain.repository.filter;

/**
 * Bộ lọc danh sách tập phim.
 *
 * @param movieId phim chứa các tập này — bắt buộc
 * @param q       từ khoá tìm theo tiêu đề tập
 */
public record EpisodeFilter(String movieId, String q) {
}
