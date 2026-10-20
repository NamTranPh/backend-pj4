package com.example.backend_pj4.presentation.account;

import com.example.backend_pj4.common.pagination.PageResult;
import org.springframework.http.ResponseEntity;
import com.example.backend_pj4.common.annotation.AuthResultUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.command.user.UpdateProfileCommand;
import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.dto.auth.SessionResult;
import com.example.backend_pj4.application.port.in.auth.GetCurrentUserUseCase;
import com.example.backend_pj4.application.port.in.auth.ListSessionsQuery;
import com.example.backend_pj4.application.port.in.auth.RevokeOtherSessionsUseCase;
import com.example.backend_pj4.application.port.in.auth.RevokeSessionUseCase;
import com.example.backend_pj4.domain.repository.filter.SessionFilter;
import com.example.backend_pj4.application.port.in.user.DeleteAvatarUseCase;
import com.example.backend_pj4.application.port.in.user.UpdateUserProfileUseCase;
import com.example.backend_pj4.presentation.account.request.UpdateProfileRequest;

import com.example.backend_pj4.infrastructure.security.AppUserDetails;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;
import com.example.backend_pj4.presentation.common.request.PageableFactory;

import com.example.backend_pj4.common.annotation.AuthRequired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Auth - Account Management")
@AuthRequired
@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final DeleteAvatarUseCase deleteAvatarUseCase;
    private final ListSessionsQuery listSessionsQuery;
    private final RevokeSessionUseCase revokeSessionUseCase;
    private final RevokeOtherSessionsUseCase revokeOtherSessionsUseCase;

    public AccountController(GetCurrentUserUseCase getCurrentUserUseCase,
                             UpdateUserProfileUseCase updateUserProfileUseCase,
                             DeleteAvatarUseCase deleteAvatarUseCase,
                             ListSessionsQuery listSessionsQuery,
                             RevokeSessionUseCase revokeSessionUseCase,
                             RevokeOtherSessionsUseCase revokeOtherSessionsUseCase) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        this.deleteAvatarUseCase = deleteAvatarUseCase;
        this.listSessionsQuery = listSessionsQuery;
        this.revokeSessionUseCase = revokeSessionUseCase;
        this.revokeOtherSessionsUseCase = revokeOtherSessionsUseCase;
    }

    @Operation(summary = "Danh sách thiết bị đang đăng nhập. Quyền truy cập: Người dùng đã đăng nhập.")
    @GetMapping("/sessions")
    public PageResult<SessionResult> listSessions(
            @AuthResultUser UserDetails userDetails,
            @ModelAttribute PageQueryParamDto params,
            @CookieValue(name = "${auth.cookie.refresh-name}", required = false) String refreshToken) {
        return listSessionsQuery.execute(
                new SessionFilter(AppUserDetails.userIdOf(userDetails)),
                refreshToken,
                PageableFactory.create(params, ListSessionsQuery.DEFAULT_SORT));
    }

    @Operation(summary = "Đăng xuất khỏi tất cả các thiết bị khác (giữ lại thiết bị hiện tại). Quyền truy cập: Người dùng đã đăng nhập.")
    @DeleteMapping("/sessions/other")
    public ResponseEntity<Void> revokeOtherSessions(
            @AuthResultUser UserDetails userDetails,
            @CookieValue(name = "${auth.cookie.refresh-name}", required = false) String refreshToken) {
        revokeOtherSessionsUseCase.execute(AppUserDetails.userIdOf(userDetails), refreshToken);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Đăng xuất một thiết bị khác. Quyền truy cập: Người dùng đã đăng nhập.")
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> revokeSession(
            @AuthResultUser UserDetails userDetails,
            @PathVariable String sessionId) {
        revokeSessionUseCase.execute(AppUserDetails.userIdOf(userDetails), sessionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lấy thông tin profile cá nhân của tôi. Quyền truy cập: Người dùng đã đăng nhập.")
    @GetMapping("/me")
    public ResponseEntity<UserProfileResult> getMe(@AuthResultUser UserDetails userDetails) {
        return ResponseEntity.ok(getCurrentUserUseCase.execute(userDetails.getUsername()));
    }

    @Operation(summary = "Cập nhật thông tin profile cá nhân. Quyền truy cập: Người dùng đã đăng nhập.")
    @PatchMapping("/me")
    public ResponseEntity<UserProfileResult> updateMe(@Valid @RequestBody UpdateProfileRequest request,
                                                       @AuthResultUser UserDetails userDetails) {
        String userId = AppUserDetails.userIdOf(userDetails);
        UserProfileResult result = updateUserProfileUseCase.execute(
                new UpdateProfileCommand(userId, request.name(), request.phone(),
                        request.address(), request.profileUrl()));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Xóa ảnh đại diện avatar cá nhân. Quyền truy cập: Người dùng đã đăng nhập.")
    @DeleteMapping("/me/avatar")
    public ResponseEntity<Void> deleteAvatar(@AuthResultUser UserDetails userDetails) {
        String userId = AppUserDetails.userIdOf(userDetails);
        deleteAvatarUseCase.execute(userId);
        return ResponseEntity.ok().build();
    }
}
