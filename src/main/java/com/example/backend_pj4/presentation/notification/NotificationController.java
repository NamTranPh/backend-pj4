package com.example.backend_pj4.presentation.notification;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.common.annotation.AuthResultUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.notification.NotificationResult;
import com.example.backend_pj4.application.port.in.notification.CountUnreadNotificationsUseCase;
import com.example.backend_pj4.application.port.in.notification.ListUserNotificationsQuery;
import com.example.backend_pj4.domain.repository.filter.NotificationFilter;
import com.example.backend_pj4.application.port.in.notification.MarkNotificationReadUseCase;
import com.example.backend_pj4.infrastructure.security.AppUserDetails;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;
import com.example.backend_pj4.presentation.common.request.PageableFactory;

import com.example.backend_pj4.common.annotation.AuthRequired;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Notifications")
@AuthRequired
@RestController
@RequestMapping("/api/v1/me/notifications")
public class NotificationController {

    private final ListUserNotificationsQuery listUserNotificationsQuery;
    private final MarkNotificationReadUseCase markNotificationReadUseCase;
    private final CountUnreadNotificationsUseCase countUnreadNotificationsUseCase;

    public NotificationController(
            ListUserNotificationsQuery listUserNotificationsQuery,
            MarkNotificationReadUseCase markNotificationReadUseCase,
            CountUnreadNotificationsUseCase countUnreadNotificationsUseCase
    ) {
        this.listUserNotificationsQuery = listUserNotificationsQuery;
        this.markNotificationReadUseCase = markNotificationReadUseCase;
        this.countUnreadNotificationsUseCase = countUnreadNotificationsUseCase;
    }

    @Operation(summary = "Lấy danh sách thông báo của người dùng hiện tại.")
    @GetMapping
    public PageResult<NotificationResult> list(
            @AuthResultUser UserDetails userDetails,
            @ModelAttribute PageQueryParamDto params,
            @RequestParam(required = false) Boolean isRead
    ) {
        return listUserNotificationsQuery.execute(
                new NotificationFilter(resolveUserId(userDetails), params.search(), isRead),
                PageableFactory.create(params,
                        ListUserNotificationsQuery.SORTABLE, ListUserNotificationsQuery.DEFAULT_SORT));
    }

    @Operation(summary = "Đếm số thông báo chưa đọc.")
    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(@AuthResultUser UserDetails userDetails) {
        String userId = resolveUserId(userDetails);
        return Map.of("unreadCount", countUnreadNotificationsUseCase.execute(userId));
    }

    @Operation(summary = "Đánh dấu thông báo đã đọc.")
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String id,
            @AuthResultUser UserDetails userDetails
    ) {
        String userId = resolveUserId(userDetails);
        markNotificationReadUseCase.execute(id, userId);
        return ResponseEntity.ok().build();
    }

    /** userId lấy thẳng từ principal — filter đã nạp sẵn, không cần tra lại database. */
    private String resolveUserId(UserDetails userDetails) {
        return AppUserDetails.userIdOf(userDetails);
    }
}
