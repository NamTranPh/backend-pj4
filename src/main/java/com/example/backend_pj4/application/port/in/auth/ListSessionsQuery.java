package com.example.backend_pj4.application.port.in.auth;

import com.example.backend_pj4.application.dto.auth.SessionResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.SessionFilter;

/** Cổng ĐỌC danh sách phiên đăng nhập. */
public interface ListSessionsQuery {

    /** Thiết bị sắp xếp theo lần dùng gần nhất; client không đổi được thứ tự này. */
    String DEFAULT_SORT = "lastUsedAt";

    /**
     * @param currentRefreshToken refresh token trong cookie của request hiện tại, dùng để đánh dấu
     *                            phiên nào là thiết bị này. Có thể null.
     */
    PageResult<SessionResult> execute(SessionFilter filter, String currentRefreshToken, PageQuery query);
}
