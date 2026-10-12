package com.example.backend_pj4.domain.repository.filter;

/**
 * Bộ lọc danh sách phiên đăng nhập.
 *
 * <p>Chỉ có chủ sở hữu: luôn trả về phiên còn hiệu lực, không cho lọc thêm — người dùng không
 * có lý do gì để xem phiên đã thu hồi.
 */
public record SessionFilter(String userId) {
}
