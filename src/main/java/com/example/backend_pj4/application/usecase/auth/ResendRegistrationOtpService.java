package com.example.backend_pj4.application.usecase.auth;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.ResendRegistrationOtpCommand;
import com.example.backend_pj4.application.port.in.auth.ResendRegistrationOtpUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.constants.enums.OtpType;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.OtpVerificationRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class ResendRegistrationOtpService implements ResendRegistrationOtpUseCase {

    private final UserRepository userRepository;
    private final OtpVerificationRepository otpRepository;
    private final OtpHelper otpHelper;

    public ResendRegistrationOtpService(UserRepository userRepository,
                                         OtpVerificationRepository otpRepository,
                                         OtpHelper otpHelper) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.otpHelper = otpHelper;
    }

    @Override
    @Transactional
    public void execute(ResendRegistrationOtpCommand command) {
        String email = command.email().toLowerCase().trim();

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getAccountStatus() != AccountStatus.INACTIVE || Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        otpRepository.findByEmailAndType(email, OtpType.REGISTRATION).ifPresent(existing -> {
            if (existing.getCreatedAt() != null) {
                long elapsed = Duration.between(existing.getCreatedAt(), LocalDateTime.now()).getSeconds();
                if (elapsed < otpHelper.getResendCooldownSeconds()) {
                    throw new CustomException(ErrorCode.OTP_RESEND_TOO_SOON);
                }
            }
        });

        otpHelper.issueOtp(email, OtpType.REGISTRATION);
    }
}
