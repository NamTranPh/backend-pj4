package com.example.backend_pj4.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Độ dài mật khẩu tối thiểu, tách riêng theo loại tài khoản.
 *
 * <p>Tài khoản admin do quản trị viên tự tạo và quản lý nên giữ ngưỡng cơ bản; tài khoản
 * người dùng mở công khai cho đăng ký nên siết chặt hơn.
 */
@Data
@ConfigurationProperties(prefix = "auth.password")
public class PasswordPolicyProperties {
    private int minLengthUser;
    private int minLengthAdmin;
}
