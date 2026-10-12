package com.example.backend_pj4.application.port.in.notification;

import java.util.Set;

import com.example.backend_pj4.application.dto.notification.NotificationResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.NotificationFilter;

/** Cổng ĐỌC danh sách thông báo. */
public interface ListUserNotificationsQuery {

    Set<String> SORTABLE = Set.of("createdAt", "isRead");
    String DEFAULT_SORT = "createdAt";

    PageResult<NotificationResult> execute(NotificationFilter filter, PageQuery query);
}
