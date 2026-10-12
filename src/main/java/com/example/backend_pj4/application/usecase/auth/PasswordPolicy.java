package com.example.backend_pj4.application.usecase.auth;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.infrastructure.config.properties.PasswordPolicyProperties;

/**
 * Kiểm tra độ dài mật khẩu theo loại tài khoản.
 *
 * <p><b>Vì sao không dùng {@code @Size} trên request DTO:</b> {@code /api/v1/auth/reset-password}
 * và {@code /api/v1/auth/change-password} dùng chung cho cả tài khoản người dùng lẫn admin, nên
 * một ràng buộc tĩnh trên DTO không phân biệt được hai nhóm. Đây là luật nghiệp vụ phụ thuộc dữ
 * liệu (role của tài khoản), đúng chỗ của nó là tầng application — xem AGENTS.md §22.
 *
 * <p>Riêng {@code RegisterRequest} vẫn giữ {@code @Size} vì đăng ký công khai luôn tạo
 * {@code role=USER}, không có nhánh nào khác.
 */
@Service
public class PasswordPolicy {

    private final PasswordPolicyProperties properties;

    public PasswordPolicy(PasswordPolicyProperties properties) {
        this.properties = properties;
    }

    public void assertValid(User user, String rawPassword) {
        int min = minLengthFor(user.getRole());
        if (rawPassword == null || rawPassword.length() < min) {
            throw new CustomException(ErrorCode.PASSWORD_TOO_SHORT);
        }
    }

    public int minLengthFor(UserRole role) {
        return role == UserRole.ADMIN
                ? properties.getMinLengthAdmin()
                : properties.getMinLengthUser();
    }
}
