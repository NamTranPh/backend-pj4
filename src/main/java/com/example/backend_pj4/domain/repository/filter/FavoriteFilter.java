package com.example.backend_pj4.domain.repository.filter;

/**
 * Bộ lọc danh sách phim yêu thích.
 *
 * @param userId chủ sở hữu danh sách — bắt buộc
 * @param q      từ khoá tìm theo <b>tên phim</b>, phải JOIN sang bảng movie
 */
public record FavoriteFilter(String userId, String q) {
}
