package com.example.backend_pj4.domain.repository.filter;

/**
 * Bộ lọc danh sách gói thành viên.
 *
 * @param q          từ khoá tìm theo tên gói
 * @param activeOnly true thì chỉ lấy gói đang bán
 */
public record MembershipPlanFilter(String q, boolean activeOnly) {
}
