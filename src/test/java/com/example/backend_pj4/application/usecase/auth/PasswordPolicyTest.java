package com.example.backend_pj4.application.usecase.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.infrastructure.config.properties.PasswordPolicyProperties;

/**
 * Khoá chính sách độ dài mật khẩu tách theo loại tài khoản: đăng ký công khai siết 8 ký tự,
 * tài khoản admin do quản trị viên tự tạo nên giữ ngưỡng cơ bản 6.
 *
 * <p>Luật này phụ thuộc role của tài khoản nên không đặt được bằng {@code @Size} trên DTO —
 * {@code /auth/change-password} và {@code /auth/reset-password} dùng chung cho cả hai nhóm.
 */
class PasswordPolicyTest {

    private PasswordPolicy policy;

    @BeforeEach
    void setUp() {
        PasswordPolicyProperties props = new PasswordPolicyProperties();
        props.setMinLengthUser(8);
        props.setMinLengthAdmin(6);
        policy = new PasswordPolicy(props);
    }

    private User userWithRole(UserRole role) {
        return User.builder().id("u1").email("a@b.c").role(role).build();
    }

    private ErrorCode errorOf(Runnable action) {
        try {
            action.run();
            throw new AssertionError("mong đợi CustomException nhưng không có lỗi nào");
        } catch (CustomException e) {
            return e.getErrorCode();
        }
    }

    @Test
    void nguoiDung_duoi8KyTu_biTuChoi() {
        User user = userWithRole(UserRole.USER);

        assertThat(errorOf(() -> policy.assertValid(user, "1234567")))
                .isEqualTo(ErrorCode.PASSWORD_TOO_SHORT);
    }

    @Test
    void nguoiDung_du8KyTu_duocChapNhan() {
        assertThatCode(() -> policy.assertValid(userWithRole(UserRole.USER), "12345678"))
                .doesNotThrowAnyException();
    }

    @Test
    void admin_6KyTu_vanDuocChapNhan() {
        assertThatCode(() -> policy.assertValid(userWithRole(UserRole.ADMIN), "123456"))
                .as("tài khoản admin giữ ngưỡng cơ bản theo yêu cầu")
                .doesNotThrowAnyException();
    }

    @Test
    void admin_duoi6KyTu_vanBiTuChoi() {
        assertThat(errorOf(() -> policy.assertValid(userWithRole(UserRole.ADMIN), "12345")))
                .isEqualTo(ErrorCode.PASSWORD_TOO_SHORT);
    }

    @Test
    void matKhauNull_biTuChoi() {
        assertThat(errorOf(() -> policy.assertValid(userWithRole(UserRole.USER), null)))
                .isEqualTo(ErrorCode.PASSWORD_TOO_SHORT);
    }

    @Test
    void nguongTraVeDungTheoRole() {
        assertThat(policy.minLengthFor(UserRole.USER)).isEqualTo(8);
        assertThat(policy.minLengthFor(UserRole.ADMIN)).isEqualTo(6);
    }
}
