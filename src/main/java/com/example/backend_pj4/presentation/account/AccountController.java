package com.example.backend_pj4.presentation.account;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.command.user.UpdateProfileCommand;
import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.port.in.auth.GetCurrentUserUseCase;
import com.example.backend_pj4.application.port.in.user.DeleteAvatarUseCase;
import com.example.backend_pj4.application.port.in.user.UpdateUserProfileUseCase;
import com.example.backend_pj4.presentation.account.request.UpdateProfileRequest;

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

    public AccountController(GetCurrentUserUseCase getCurrentUserUseCase,
                             UpdateUserProfileUseCase updateUserProfileUseCase,
                             DeleteAvatarUseCase deleteAvatarUseCase) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        this.deleteAvatarUseCase = deleteAvatarUseCase;
    }

    @Operation(summary = "Lấy thông tin profile cá nhân của tôi. Quyền truy cập: Người dùng đã đăng nhập.")
    @GetMapping("/me")
    public ResponseEntity<UserProfileResult> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(getCurrentUserUseCase.execute(userDetails.getUsername()));
    }

    @Operation(summary = "Cập nhật thông tin profile cá nhân. Quyền truy cập: Người dùng đã đăng nhập.")
    @PatchMapping("/me")
    public ResponseEntity<UserProfileResult> updateMe(@Valid @RequestBody UpdateProfileRequest request,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        String userId = getCurrentUserUseCase.execute(userDetails.getUsername()).id();
        UserProfileResult result = updateUserProfileUseCase.execute(
                new UpdateProfileCommand(userId, request.name(), request.phone(),
                        request.address(), request.profileUrl()));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Xóa ảnh đại diện avatar cá nhân. Quyền truy cập: Người dùng đã đăng nhập.")
    @DeleteMapping("/me/avatar")
    public ResponseEntity<Void> deleteAvatar(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = getCurrentUserUseCase.execute(userDetails.getUsername()).id();
        deleteAvatarUseCase.execute(userId);
        return ResponseEntity.ok().build();
    }
}
