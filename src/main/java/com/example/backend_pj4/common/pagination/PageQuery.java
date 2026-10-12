package com.example.backend_pj4.common.pagination;

import com.example.backend_pj4.common.constants.enums.SortOrder;

/**
 * Yêu cầu phân trang đã được chuẩn hoá — Java thuần, không phụ thuộc framework.
 *
 * <p>Thay cho {@code org.springframework.data.domain.Pageable} ở tầng domain và application.
 * AGENTS.md §3 cấm domain import {@code org.springframework.*}; §28 đề xuất đúng cặp
 * {@code PageQuery} / {@link PageResult}. Việc đổi sang {@code Pageable} của Spring Data chỉ
 * xảy ra ở một nơi duy nhất: {@code PageableMapper} trong infrastructure.
 *
 * @param page      <b>đếm từ 0</b> — đi thẳng vào {@code PageRequest.of()}. URL đếm từ 1,
 *                  {@code PageableFactory} đã trừ đi. Khác với {@link PageResult#page()} đếm từ 1
 * @param size      số dòng mỗi trang; {@code PageableFactory} áp mặc định 15, trần 50
 * @param sortBy    tên field sắp xếp, đã qua whitelist của module
 * @param direction chiều sắp xếp
 */
public record PageQuery(int page, int size, String sortBy, SortOrder direction) {
}
