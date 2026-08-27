package com.example.backend_pj4.presentation.notification;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.notification.NotificationResult;
import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.port.in.notification.CountUnreadNotificationsUseCase;
import com.example.backend_pj4.application.port.in.notification.ListUserNotificationsUseCase;
import com.example.backend_pj4.application.port.in.notification.MarkNotificationReadUseCase;
import com.example.backend_pj4.application.port.in.auth.GetCurrentUserUseCase;
import com.example.backend_pj4.common.annotation.AuthRequired;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Notifications")
@AuthRequired
@RestController
@RequestMapping("/api/v1/me/notifications")
public class NotificationController {

    private final ListUserNotificationsUseCase listUserNotificationsUseCase;
    private final MarkNotificationReadUseCase markNotificationReadUseCase;
    private final CountUnreadNotificationsUseCase countUnreadNotificationsUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public NotificationController(
            ListUserNotificationsUseCase listUserNotificationsUseCase,
            MarkNotificationReadUseCase markNotificationReadUseCase,
            CountUnreadNotificationsUseCase countUnreadNotificationsUseCase,
            GetCurrentUserUseCase getCurrentUserUseCase
    ) {
        this.listUserNotificationsUseCase = listUserNotificationsUseCase;
        this.markNotificationReadUseCase = markNotificationReadUseCase;
        this.countUnreadNotificationsUseCase = countUnreadNotificationsUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @Operation(summary = "Lấy danh sách thông báo của người dùng hiện tại.")
    @GetMapping
    public List<NotificationResult> list(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = resolveUserId(userDetails);
        return listUserNotificationsUseCase.execute(userId);
    }

    @Operation(summary = "Đếm số thông báo chưa đọc.")
    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = resolveUserId(userDetails);
        return Map.of("unreadCount", countUnreadNotificationsUseCase.execute(userId));
    }

    @Operation(summary = "Đánh dấu thông báo đã đọc.")
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = resolveUserId(userDetails);
        markNotificationReadUseCase.execute(id, userId);
        return ResponseEntity.ok().build();
    }

    private String resolveUserId(UserDetails userDetails) {
        UserProfileResult user = getCurrentUserUseCase.execute(userDetails.getUsername());
        return user.id();
    }
}
