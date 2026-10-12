package com.example.backend_pj4.common.pagination;

import java.util.List;
import java.util.function.Function;

/**
 * Một trang kết quả — Java thuần, không phụ thuộc framework.
 *
 * <p>Thay cho {@code org.springframework.data.domain.Page} ở tầng domain và application.
 *
 * <p>Giữ đủ 5 trường thay vì chỉ {@code (items, totalItems)}: {@code ApiResponseAdvice} bọc
 * response <b>tự động</b> nên không biết {@code page}/{@code size} nếu bản thân kết quả không
 * mang theo. Rút gọn còn 2 trường sẽ đẩy phép tính {@code totalPages} vào từng query handler.
 *
 * @param page <b>đếm từ 1</b> — là số client nhìn thấy. Khác với {@link PageQuery#page()} đếm từ 0;
 *             phép {@code +1} nằm ở đúng một chỗ là {@code PageableMapper.toPageResult}
 */
public record PageResult<T>(List<T> items, long totalItems, int page, int size, int totalPages) {

    /** Trang rỗng, giữ nguyên thông tin trang đã yêu cầu. */
    public static <T> PageResult<T> empty(PageQuery query) {
        return new PageResult<>(List.of(), 0L, query.page() + 1, query.size(), 0);
    }

    /** Đổi kiểu từng phần tử, giữ nguyên thông tin phân trang. Tương đương {@code Page.map()}. */
    public <R> PageResult<R> map(Function<T, R> mapper) {
        return withItems(items.stream().map(mapper).toList());
    }

    /**
     * Thay danh sách phần tử, giữ nguyên thông tin phân trang.
     *
     * <p>Dùng khi phải <b>lọc bớt</b> phần tử (ví dụ bỏ dòng trỏ tới phim đã xoá), lúc đó
     * {@code map} không dùng được vì nó giữ nguyên số phần tử. {@code totalItems} vẫn là tổng
     * số bản ghi database đếm được, không phải {@code newItems.size()}.
     */
    public <R> PageResult<R> withItems(List<R> newItems) {
        return new PageResult<>(newItems, totalItems, page, size, totalPages);
    }
}
