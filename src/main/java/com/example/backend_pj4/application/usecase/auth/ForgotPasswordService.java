package com.example.backend_pj4.application.usecase.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.ForgotPasswordCommand;
import com.example.backend_pj4.application.port.in.auth.ForgotPasswordUseCase;
import com.example.backend_pj4.common.constants.enums.OtpType;
import com.example.backend_pj4.domain.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ForgotPasswordService implements ForgotPasswordUseCase {

    private final UserRepository userRepository;
    private final OtpHelper otpHelper;

    public ForgotPasswordService(UserRepository userRepository, OtpHelper otpHelper) {
        this.userRepository = userRepository;
        this.otpHelper = otpHelper;
    }

    @Override
    @Transactional
    public void execute(ForgotPasswordCommand command) {
        String email = command.email().toLowerCase().trim();

        // Silent success if email doesn't exist — don't reveal whether an account exists.
        // Còn trong cooldown thì cũng im lặng bỏ qua, không ném lỗi (ném sẽ tiết lộ email
        // nào có tài khoản), nhưng vẫn chặn được spam mail.
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            if (otpHelper.isWithinResendCooldown(email, OtpType.PASSWORD_RESET)) {
                log.debug("Forgot-password OTP suppressed by cooldown");
                return;
            }
            otpHelper.issueOtp(email, OtpType.PASSWORD_RESET);
        });
    }
}
