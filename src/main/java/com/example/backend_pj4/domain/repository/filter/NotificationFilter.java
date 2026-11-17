package com.example.backend_pj4.domain.repository.filter;

/**
 * @param isRead null là lấy cả đã đọc lẫn chưa đọc
 */
public record NotificationFilter(String userId, String q, Boolean isRead) {
}
