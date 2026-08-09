package com.example.backend_pj4.presentation.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    // Đăng ký tài khoản mới bằng email + password
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Xác thực OTP đăng ký tài khoản
    @PostMapping("/verify-registration")
    public ResponseEntity<?> verifyRegistration(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Gửi lại OTP đăng ký tài khoản
    @PostMapping("/resend-registration-otp")
    public ResponseEntity<?> resendRegistrationOtp(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Đăng nhập người dùng
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Cấp lại access token từ refresh token cookie
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @CookieValue(name = "${auth.cookie.refresh-name}", required = false) String refreshTokenCookie) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Đăng xuất người dùng, xóa refresh token cookie
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = "${auth.cookie.refresh-name}", required = false) String refreshTokenCookie) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Yêu cầu OTP quên mật khẩu
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Đặt lại mật khẩu bằng OTP
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy thông tin người dùng hiện tại đang đăng nhập
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Đổi mật khẩu người dùng hiện tại
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
