package com.example.backend_pj4.application.usecase.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.ResendRegistrationOtpCommand;
import com.example.backend_pj4.application.port.in.auth.ResendRegistrationOtpUseCase;
import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.constants.enums.OtpType;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResendRegistrationOtpService implements ResendRegistrationOtpUseCase {

    private final UserRepository userRepository;
    private final OtpHelper otpHelper;

    public ResendRegistrationOtpService(UserRepository userRepository,
                                         OtpHelper otpHelper) {
        this.userRepository = userRepository;
        this.otpHelper = otpHelper;
    }

    /**
     * Luôn trả về thành công, giống {@link ForgotPasswordService}.
     * <p>
     * Trước đây endpoint công khai này ném {@code USER_NOT_FOUND} (404) rồi
     * {@code EMAIL_ALREADY_EXISTS} (409), nên chỉ cần đọc mã lỗi là phân biệt được ba
     * trạng thái: email chưa đăng ký / đang chờ xác thực / đã kích hoạt. Đó là công cụ dò
     * danh sách email người dùng của hệ thống.
     */
    @Override
    @Transactional
    public void execute(ResendRegistrationOtpCommand command) {
        String email = command.email().toLowerCase().trim();

        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
        if (user == null) {
            log.debug("Resend registration OTP bỏ qua: email chưa đăng ký");
            return;
        }
        if (user.getAccountStatus() != AccountStatus.INACTIVE || Boolean.TRUE.equals(user.getEmailVerified())) {
            log.debug("Resend registration OTP bỏ qua: tài khoản đã kích hoạt");
            return;
        }
        if (otpHelper.isWithinResendCooldown(email, OtpType.REGISTRATION)) {
            log.debug("Resend registration OTP bỏ qua: còn trong cooldown");
            return;
        }

        otpHelper.issueOtp(email, OtpType.REGISTRATION);
    }
}
