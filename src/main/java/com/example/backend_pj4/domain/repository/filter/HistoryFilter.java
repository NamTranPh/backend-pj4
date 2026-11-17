package com.example.backend_pj4.domain.repository.filter;

/**
 * @param unfinishedOnly true thì chỉ lấy phim đang xem dở — thay cho method
 *                       {@code findUnfinishedByUserId} riêng. Điều kiện này phải nằm TRONG câu
 *                       query, lọc bằng Java sau khi lấy sẽ làm {@code totalItems} sai
 */
public record HistoryFilter(String userId, String q, boolean unfinishedOnly) {
}
