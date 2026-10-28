package com.example.backend_pj4.presentation.account;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend_pj4.application.command.auth.ChangePasswordCommand;
import com.example.backend_pj4.application.command.upload.UploadFileCommand;
import com.example.backend_pj4.application.command.user.UpdateProfileCommand;
import com.example.backend_pj4.application.dto.auth.SessionResult;
import com.example.backend_pj4.application.dto.upload.UploadResult;
import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.port.in.auth.ChangePasswordUseCase;
import com.example.backend_pj4.application.port.in.auth.GetCurrentUserUseCase;
import com.example.backend_pj4.application.port.in.auth.ListSessionsQuery;
import com.example.backend_pj4.application.port.in.auth.RevokeOtherSessionsUseCase;
import com.example.backend_pj4.application.port.in.auth.RevokeSessionUseCase;
import com.example.backend_pj4.application.port.in.upload.UploadFileUseCase;
import com.example.backend_pj4.application.port.in.user.DeleteAvatarUseCase;
import com.example.backend_pj4.application.port.in.user.UpdateUserProfileUseCase;
import com.example.backend_pj4.common.annotation.AuthRequired;
import com.example.backend_pj4.common.annotation.AuthResultUser;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.UploadType;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.SessionFilter;
import com.example.backend_pj4.infrastructure.security.AppUserDetails;
import com.example.backend_pj4.presentation.account.request.UpdateProfileRequest;
import com.example.backend_pj4.presentation.auth.request.ChangePasswordRequest;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;
import com.example.backend_pj4.presentation.common.request.PageableFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Account Management - Admin")
@AuthRequired
@RestController
@RequestMapping("/api/v1/admin/account")
public class AdminAccountController {

    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final DeleteAvatarUseCase deleteAvatarUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ListSessionsQuery listSessionsQuery;
    private final RevokeSessionUseCase revokeSessionUseCase;
    private final RevokeOtherSessionsUseCase revokeOtherSessionsUseCase;
    private final UploadFileUseCase uploadFileUseCase;

    public AdminAccountController(
            GetCurrentUserUseCase getCurrentUserUseCase,
            UpdateUserProfileUseCase updateUserProfileUseCase,
            DeleteAvatarUseCase deleteAvatarUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            ListSessionsQuery listSessionsQuery,
            RevokeSessionUseCase revokeSessionUseCase,
            RevokeOtherSessionsUseCase revokeOtherSessionsUseCase,
            UploadFileUseCase uploadFileUseCase) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        this.deleteAvatarUseCase = deleteAvatarUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.listSessionsQuery = listSessionsQuery;
        this.revokeSessionUseCase = revokeSessionUseCase;
        this.revokeOtherSessionsUseCase = revokeOtherSessionsUseCase;
        this.uploadFileUseCase = uploadFileUseCase;
    }

    @Operation(summary = "Lấy thông tin profile Admin hiện tại. Quyền truy cập: ADMIN.")
    @GetMapping("/me")
    public ResponseEntity<UserProfileResult> getMe(@AuthResultUser UserDetails userDetails) {
        return ResponseEntity.ok(getCurrentUserUseCase.execute(userDetails.getUsername()));
    }

    @Operation(summary = "Cập nhật thông tin profile cá nhân của Admin. Quyền truy cập: ADMIN.")
    @PatchMapping("/me")
    public ResponseEntity<UserProfileResult> updateMe(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthResultUser UserDetails userDetails) {
        String userId = AppUserDetails.userIdOf(userDetails);
        UserProfileResult result = updateUserProfileUseCase.execute(
                new UpdateProfileCommand(userId, request.name(), request.phone(),
                        request.address(), request.profileUrl()));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Upload ảnh đại diện avatar cho Admin. Quyền truy cập: ADMIN.")
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResult> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthResultUser UserDetails userDetails) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        String userId = AppUserDetails.userIdOf(userDetails);
        UserRole userRole = AppUserDetails.roleOf(userDetails);

        UploadResult uploadResult = uploadFileUseCase.execute(new UploadFileCommand(
                file.getInputStream(),
                file.getSize(),
                file.getContentType(),
                file.getOriginalFilename(),
                UploadType.AVATAR,
                userRole,
                userId
        ));

        // Cập nhật URL ảnh vào thông tin cá nhân của Admin
        UserProfileResult updatedProfile = updateUserProfileUseCase.execute(
                new UpdateProfileCommand(userId, null, null, null, uploadResult.previewUrl()));

        return ResponseEntity.ok(updatedProfile);
    }

    @Operation(summary = "Xóa ảnh đại diện avatar của Admin. Quyền truy cập: ADMIN.")
    @DeleteMapping("/avatar")
    public ResponseEntity<Void> deleteAvatar(@AuthResultUser UserDetails userDetails) {
        String userId = AppUserDetails.userIdOf(userDetails);
        deleteAvatarUseCase.execute(userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Đổi mật khẩu tài khoản Admin. Quyền truy cập: ADMIN.")
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthResultUser UserDetails userDetails) {
        String userId = AppUserDetails.userIdOf(userDetails);
        changePasswordUseCase.execute(
                new ChangePasswordCommand(userId, request.oldPassword(), request.newPassword()));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Danh sách thiết bị đang đăng nhập của Admin. Quyền truy cập: ADMIN.")
    @GetMapping("/sessions")
    public PageResult<SessionResult> listSessions(
            @AuthResultUser UserDetails userDetails,
            @ModelAttribute PageQueryParamDto params,
            @CookieValue(name = "${auth.cookie.admin-refresh-name}", required = false) String refreshToken) {
        return listSessionsQuery.execute(
                new SessionFilter(AppUserDetails.userIdOf(userDetails)),
                refreshToken,
                PageableFactory.create(params, ListSessionsQuery.DEFAULT_SORT));
    }

    @Operation(summary = "Đăng xuất khỏi tất cả các thiết bị khác (giữ lại thiết bị hiện tại). Quyền truy cập: ADMIN.")
    @DeleteMapping("/sessions/other")
    public ResponseEntity<Void> revokeOtherSessions(
            @AuthResultUser UserDetails userDetails,
            @CookieValue(name = "${auth.cookie.admin-refresh-name}", required = false) String refreshToken) {
        revokeOtherSessionsUseCase.execute(AppUserDetails.userIdOf(userDetails), refreshToken);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Đăng xuất một thiết bị khác của Admin. Quyền truy cập: ADMIN.")
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> revokeSession(
            @AuthResultUser UserDetails userDetails,
            @PathVariable String sessionId) {
        revokeSessionUseCase.execute(AppUserDetails.userIdOf(userDetails), sessionId);
        return ResponseEntity.noContent().build();
    }
}
