package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.backend_pj4.common.constants.enums.SortOrder;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;

/**
 * Phiên dịch giữa phân trang thuần Java ({@link PageQuery} / {@link PageResult}) và Spring Data
 * ({@link Pageable} / {@link Page}).
 *
 * <p>Đây là nơi <b>duy nhất</b> ở ranh giới domain↔infrastructure biết tới Spring Data. Nhờ vậy
 * {@code domain/} và {@code application/} không import {@code org.springframework.*} (AGENTS.md
 * §3, §28), còn 9 adapter không ai phải tự viết lại phép chuyển đổi.
 */
public final class PageableMapper {

    private PageableMapper() {
    }

    public static Pageable toPageable(PageQuery query) {
        Sort.Direction direction = query.direction() == SortOrder.asc
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(query.page(), query.size(), Sort.by(direction, query.sortBy()));
    }

    /**
     * Đổi {@code Page<Entity>} thành {@code PageResult<Domain>}, áp {@code mapper} cho từng phần tử.
     *
     * <p><b>Phép {@code +1} nằm ở đây và chỉ ở đây:</b> Spring Data đếm trang từ 0,
     * {@link PageResult#page()} đếm từ 1 vì đó là số client nhìn thấy.
     */
    public static <E, D> PageResult<D> toPageResult(Page<E> page, Function<E, D> mapper) {
        return toPageResult(page, page.getContent().stream().map(mapper).toList());
    }

    /**
     * Biến thể nhận sẵn danh sách đã map — dùng khi phải <b>lọc bớt</b> phần tử (ví dụ bỏ dòng
     * trỏ tới phim đã xoá), lúc đó số phần tử trả về ít hơn số phần tử của {@code page}.
     *
     * <p>{@code totalItems} vẫn lấy từ {@code page} chứ không phải từ {@code items.size()}: tổng
     * số bản ghi là thứ database biết, không phải thứ đếm được trên một trang.
     */
    public static <E, D> PageResult<D> toPageResult(Page<E> page, List<D> items) {
        return new PageResult<>(
                items,
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages());
    }
}
