package com.example.backend_pj4.presentation.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.command.auth.LoginCommand;
import com.example.backend_pj4.application.command.auth.LogoutCommand;
import com.example.backend_pj4.application.command.auth.RefreshTokenCommand;
import com.example.backend_pj4.application.dto.auth.AuthTokenResult;
import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.port.in.admin.AdminLoginUseCase;
import com.example.backend_pj4.application.port.in.admin.AdminLogoutUseCase;
import com.example.backend_pj4.application.port.in.admin.AdminRefreshTokenUseCase;
import com.example.backend_pj4.application.port.in.auth.GetCurrentUserUseCase;
import com.example.backend_pj4.infrastructure.security.AuthCookieService;
import com.example.backend_pj4.presentation.auth.request.LoginRequest;
import com.example.backend_pj4.presentation.auth.response.LoginResponse;

import jakarta.servlet.http.HttpServletResponse;
import com.example.backend_pj4.common.annotation.AuthRequired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Auth - Admin")
@RestController
@RequestMapping("/api/v1/admin/auth")
public class AdminAuthController {

    private final AdminLoginUseCase adminLoginUseCase;
    private final AdminRefreshTokenUseCase adminRefreshTokenUseCase;
    private final AdminLogoutUseCase adminLogoutUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final AuthCookieService authCookieService;

    public AdminAuthController(AdminLoginUseCase adminLoginUseCase,
                                AdminRefreshTokenUseCase adminRefreshTokenUseCase,
                                AdminLogoutUseCase adminLogoutUseCase,
                                GetCurrentUserUseCase getCurrentUserUseCase,
                                AuthCookieService authCookieService) {
        this.adminLoginUseCase = adminLoginUseCase;
        this.adminRefreshTokenUseCase = adminRefreshTokenUseCase;
        this.adminLogoutUseCase = adminLogoutUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.authCookieService = authCookieService;
    }

    @Operation(summary = "Đăng nhập tài khoản Quản trị viên (Admin). Quyền truy cập: Public (Công khai).")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                                HttpServletResponse response) {
        AuthTokenResult result = adminLoginUseCase.execute(
                new LoginCommand(request.email(), request.password()));
        authCookieService.addAdminRefreshCookie(response, result.refreshToken());
        return ResponseEntity.ok(new LoginResponse(result.accessToken(), result.expiresIn()));
    }

    @Operation(summary = "Làm mới Admin Access Token bằng Refresh Token Cookie. Quyền truy cập: ADMIN.")
    @AuthRequired
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(
            @CookieValue(name = "${auth.cookie.admin-refresh-name}", required = false) String refreshTokenCookie,
            HttpServletResponse response) {
        AuthTokenResult result = adminRefreshTokenUseCase.execute(
                new RefreshTokenCommand(refreshTokenCookie));
        authCookieService.addAdminRefreshCookie(response, result.refreshToken());
        return ResponseEntity.ok(new LoginResponse(result.accessToken(), result.expiresIn()));
    }

    @Operation(summary = "Đăng xuất tài khoản Admin và xóa Cookie. Quyền truy cập: ADMIN.")
    @AuthRequired
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "${auth.cookie.admin-refresh-name}", required = false) String refreshTokenCookie,
            HttpServletResponse response) {
        adminLogoutUseCase.execute(new LogoutCommand(refreshTokenCookie));
        authCookieService.clearAdminRefreshCookie(response);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Lấy thông tin profile Admin hiện tại. Quyền truy cập: ADMIN.")
    @AuthRequired
    @GetMapping("/me")
    public ResponseEntity<UserProfileResult> me(@AuthenticationPrincipal UserDetails userDetails) {
        UserProfileResult result = getCurrentUserUseCase.execute(userDetails.getUsername());
        return ResponseEntity.ok(result);
    }
}
