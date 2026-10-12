package com.example.backend_pj4.presentation.common.request;

import java.util.Set;

import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.SortOrder;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.common.pagination.PageQuery;

/**
 * Dịch {@link PageQueryParamDto} của HTTP thành {@link PageQuery} thuần Java.
 *
 * <p>Đây là nơi DUY NHẤT biết các luật phân trang. Trước đây mỗi use case tự viết lại
 * {@code PageRequest.of(page, Math.min(size, 50), Sort.by("createdAt").descending())}, nên sửa
 * một luật phải sửa hàng chục chỗ và giá trị mặc định đã trôi thành lúc 10 lúc 20.
 *
 * <p>Đặt ở tầng presentation chứ không phải application: nó nhận DTO của HTTP, nếu để ở
 * application thì application phải import presentation, ngược chiều dependency rule. Dịch query
 * param thành đối tượng truy vấn đúng là việc của controller (AGENTS.md §7).
 *
 * <p>Trả {@link PageQuery} chứ không phải {@code Pageable} của Spring Data: domain và
 * application không được phụ thuộc framework (AGENTS.md §3, §28). Việc đổi sang {@code Pageable}
 * nằm ở {@code PageableMapper} trong infrastructure.
 */
public final class PageableFactory {
    public static final int DEFAULT_SIZE = 15;
    public static final int MAX_SIZE = 50;

    private PageableFactory() {}

    /**
     * @throws CustomException {@code BAD_REQUEST} nếu {@code sortBy} không nằm trong whitelist
     */
    public static PageQuery create(PageQueryParamDto params,
                                   Set<String> allowedSortFields,
                                   String defaultSortField) {
        PageQueryParamDto safe = params != null
                ? params
                : new PageQueryParamDto(null, null, null, null, null);

        return new PageQuery(
                resolvePage(safe.page()),
                resolveSize(safe.limit()),
                resolveSortBy(safe.sortBy(), allowedSortFields, defaultSortField),
                resolveDirection(safe.sortDir()));
    }

    /** Biến thể cho module không cho sắp xếp theo ý client (ví dụ danh sách thiết bị). */
    public static PageQuery create(PageQueryParamDto params, String fixedSortField) {
        return create(params, Set.of(fixedSortField), fixedSortField);
    }

    /** URL đếm trang từ 1, Spring Data đếm từ 0. */
    private static int resolvePage(Integer page) {
        return page == null ? 0 : Math.max(0, page - 1);
    }

    private static int resolveSize(Integer limit) {
        if (limit == null || limit < 1) {
            return DEFAULT_SIZE;
        }
        return Math.min(limit, MAX_SIZE);
    }

    /**
     * <b>Whitelist là bắt buộc, không phải tuỳ chọn.</b> {@code sortBy} đi thẳng vào
     * {@code Sort.by(property)} rồi thành tên cột trong câu SQL. Không lọc thì client dò được
     * cấu trúc bảng qua phản hồi lỗi, hoặc ép sắp xếp theo cột không có index làm chậm hệ thống.
     */
    private static String resolveSortBy(String sortBy, Set<String> allowedSortFields,
                                        String defaultSortField) {
        String field = (sortBy == null || sortBy.isBlank()) ? defaultSortField : sortBy.trim();

        if (!allowedSortFields.contains(field)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        return field;
    }

    private static SortOrder resolveDirection(String sortDir) {
        return "asc".equalsIgnoreCase(sortDir) ? SortOrder.asc : SortOrder.desc;
    }
}
