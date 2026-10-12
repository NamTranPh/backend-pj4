package com.example.backend_pj4.application.port.in.user;

import java.util.Set;

import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.UserFilter;

/** Cổng ĐỌC danh sách người dùng. Nhánh đọc bỏ hậu tố UseCase để phân biệt với nhánh ghi. */
public interface ListUsersQuery {

    /** Field được phép sắp xếp. Controller đọc để kiểm tham số trước khi dựng PageQuery. */
    Set<String> SORTABLE = Set.of("createdAt", "email", "name", "lastActiveAt");
    String DEFAULT_SORT = "createdAt";

    PageResult<UserProfileResult> execute(UserFilter filter, PageQuery query);
}
