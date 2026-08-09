package com.example.backend_pj4.presentation.admin;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Luồng auth RIÊNG cho admin. Endpoint tách hẳn khỏi user, dùng cookie refresh
 * riêng (admin_refresh_token) để session admin và user không đè nhau.
 */
@RestController
@RequestMapping("/api/v1/admin/auth")
public class AdminAuthController {

    // Đăng nhập admin (chỉ tài khoản ADMIN)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Cấp lại access token admin từ refresh token cookie
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @CookieValue(name = "${auth.cookie.admin-refresh-name}", required = false) String refreshTokenCookie) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Đăng xuất admin, xóa refresh token cookie
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = "${auth.cookie.admin-refresh-name}", required = false) String refreshTokenCookie) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy thông tin admin hiện tại đang đăng nhập
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
