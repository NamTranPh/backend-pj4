package com.example.backend_pj4.application.usecase.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.LoginCommand;
import com.example.backend_pj4.application.dto.auth.AuthTokenResult;
import com.example.backend_pj4.application.port.in.admin.AdminLoginUseCase;
import com.example.backend_pj4.application.port.out.PasswordHasher;
import com.example.backend_pj4.application.usecase.auth.LoginFailureRecorder;
import com.example.backend_pj4.application.usecase.auth.LoginService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.User;

@Service
public class AdminLoginService implements AdminLoginUseCase {

    private final PasswordHasher passwordHasher;
    private final LoginService loginService;
    private final LoginFailureRecorder failureRecorder;

    public AdminLoginService(PasswordHasher passwordHasher,
                              LoginService loginService,
                              LoginFailureRecorder failureRecorder) {
        this.passwordHasher = passwordHasher;
        this.loginService = loginService;
        this.failureRecorder = failureRecorder;
    }

    /**
     * Cùng thứ tự kiểm tra với {@link LoginService#execute}: khoá tài khoản trước mật khẩu
     * (rate limit), mật khẩu trước mọi check trạng thái, và email không tồn tại vẫn tốn
     * thời gian như email có tồn tại.
     * <p>
     * Riêng check {@code role != ADMIN} bắt buộc phải nằm SAU khi mật khẩu đúng — nếu ném
     * {@code ADMIN_ROLE_REQUIRED} sớm thì chỉ cần gửi mật khẩu bừa là dò ra được tài khoản
     * nào là admin để tập trung tấn công.
     */
    @Override
    @Transactional
    public AuthTokenResult execute(LoginCommand command) {
        String email = command.email().toLowerCase().trim();

        User user = loginService.findUserOrFailUniformly(email, command.password());

        loginService.assertNotLocked(user);

        if (!passwordHasher.matches(command.password(), user.getPassword())) {
            failureRecorder.recordFailure(user);
            throw new CustomException(ErrorCode.BAD_CREDENTIALS);
        }

        // --- Từ đây caller đã chứng minh quyền sở hữu, trả mã lỗi cụ thể là an toàn ---

        if (user.getRole() != UserRole.ADMIN) {
            throw new CustomException(ErrorCode.ADMIN_ROLE_REQUIRED);
        }
        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }
        if (Boolean.TRUE.equals(user.getIsBanned())) {
            throw new CustomException(ErrorCode.ACCOUNT_BANNED);
        }
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }

        failureRecorder.resetFailures(user);

        // admin=true -> token mang claim ch=admin, dùng được cả kênh CMS và kênh user
        return loginService.issueTokens(user, true, command.userAgent(), command.ipAddress());
    }
}
