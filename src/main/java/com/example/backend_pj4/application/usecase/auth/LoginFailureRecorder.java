package com.example.backend_pj4.application.usecase.auth;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.config.properties.LoginLockProperties;

/**
 * Ghi số lần đăng nhập sai và khoá tài khoản khi vượt ngưỡng.
 *
 * <p><b>Vì sao phải là một bean riêng, không để trong {@code LoginService}:</b>
 * use case đăng nhập chạy trong {@code @Transactional}, và khi mật khẩu sai nó ném
 * {@code CustomException} — một {@code RuntimeException}. Spring rollback mặc định cho
 * {@code RuntimeException}, nên bản ghi đếm vừa lưu sẽ bị cuốn đi cùng, khiến
 * {@code failed_login_attempts} không bao giờ tăng và {@code locked_until} không bao giờ
 * được set. Trước đây lỗi này bị che vì có thêm một bộ đếm trên Redis (ghi Redis nằm ngoài
 * transaction nên sống sót); khi bỏ lớp Redis đó thì lỗi lộ ra.
 *
 * <p>{@code REQUIRES_NEW} mở một transaction riêng, commit độc lập với transaction đăng nhập.
 * Đây không phải cách che một thiết kế transaction sai (AGENTS.md §12) mà là đúng ca dùng của
 * nó: một bản ghi kiểm đếm bắt buộc phải sống sót khi nghiệp vụ chính rollback.
 *
 * <p>Phải nằm ở bean khác vì self-invocation trong cùng class không đi qua proxy của Spring,
 * annotation sẽ không có tác dụng.
 */
@Service
public class LoginFailureRecorder {

    private final UserRepository userRepository;
    private final LoginLockProperties loginLockProperties;

    public LoginFailureRecorder(UserRepository userRepository,
                                LoginLockProperties loginLockProperties) {
        this.userRepository = userRepository;
        this.loginLockProperties = loginLockProperties;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstFailure = user.getFirstFailureAt();

        // Ngoài cửa sổ đếm thì bắt đầu lại từ 1
        boolean windowExpired = firstFailure == null
                || Duration.between(firstFailure, now).toMinutes() >= loginLockProperties.getFailureWindowMinutes();

        int attempts = windowExpired || user.getFailedLoginAttempts() == null
                ? 1
                : user.getFailedLoginAttempts() + 1;
        LocalDateTime windowStart = windowExpired ? now : firstFailure;

        User.UserBuilder updated = user.toBuilder()
                .failedLoginAttempts(attempts)
                .firstFailureAt(windowStart);

        if (attempts >= loginLockProperties.getMaxFailures()) {
            updated.lockedUntil(now.plusMinutes(loginLockProperties.getLockMinutes()))
                    .failedLoginAttempts(0)
                    .firstFailureAt(null);
        }

        userRepository.save(updated.build());
    }

    /**
     * Xoá bộ đếm sau khi đăng nhập đúng. Chạy trong transaction của use case gọi nó — nếu
     * các bước sau thất bại thì rollback luôn cũng đúng, vì lần đăng nhập đó không thành công.
     */
    @Transactional
    public void resetFailures(User user) {
        boolean hasFailures = (user.getFailedLoginAttempts() != null && user.getFailedLoginAttempts() > 0)
                || user.getFirstFailureAt() != null
                || user.getLockedUntil() != null;

        if (hasFailures) {
            userRepository.save(user.toBuilder()
                    .failedLoginAttempts(0)
                    .firstFailureAt(null)
                    .lockedUntil(null)
                    .build());
        }
    }
}
