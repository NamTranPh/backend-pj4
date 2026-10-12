package com.example.backend_pj4.domain.repository.filter;

/**
 * Bộ lọc danh sách thông báo.
 *
 * @param userId chủ sở hữu — bắt buộc
 * @param q      từ khoá tìm theo tiêu đề hoặc nội dung
 * @param isRead null là lấy cả đã đọc lẫn chưa đọc
 */
public record NotificationFilter(String userId, String q, Boolean isRead) {
}
