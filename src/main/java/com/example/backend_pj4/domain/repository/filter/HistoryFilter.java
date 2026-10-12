package com.example.backend_pj4.domain.repository.filter;

/**
 * Bộ lọc lịch sử xem.
 *
 * @param userId         chủ sở hữu lịch sử — bắt buộc
 * @param q              từ khoá tìm theo <b>tên phim</b>, phải JOIN sang bảng movie
 * @param unfinishedOnly true thì chỉ lấy phim đang xem dở — thay cho method
 *                       {@code findUnfinishedByUserId} riêng. Điều kiện này phải nằm TRONG câu
 *                       query, lọc bằng Java sau khi lấy sẽ làm {@code totalItems} sai
 */
public record HistoryFilter(String userId, String q, boolean unfinishedOnly) {
}
