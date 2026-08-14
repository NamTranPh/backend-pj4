package com.example.backend_pj4.application.usecase.auth;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.ResetPasswordCommand;
import com.example.backend_pj4.application.port.in.auth.ResetPasswordUseCase;
import com.example.backend_pj4.application.port.out.PasswordHasher;
import com.example.backend_pj4.application.port.out.RefreshTokenStore;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.OtpType;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.OtpVerification;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.OtpVerificationRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class ResetPasswordService implements ResetPasswordUseCase {

    private final UserRepository userRepository;
    private final OtpVerificationRepository otpRepository;
    private final OtpHelper otpHelper;
    private final PasswordHasher passwordHasher;
    private final RefreshTokenStore refreshTokenStore;

    public ResetPasswordService(UserRepository userRepository,
                                 OtpVerificationRepository otpRepository,
                                 OtpHelper otpHelper,
                                 PasswordHasher passwordHasher,
                                 RefreshTokenStore refreshTokenStore) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.otpHelper = otpHelper;
        this.passwordHasher = passwordHasher;
        this.refreshTokenStore = refreshTokenStore;
    }

    @Override
    @Transactional
    public void execute(ResetPasswordCommand command) {
        String email = command.email().toLowerCase().trim();

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        OtpVerification otp = otpRepository.findByEmailAndType(email, OtpType.PASSWORD_RESET)
                .orElseThrow(() -> new CustomException(ErrorCode.OTP_INVALID));

        if (otp.getUsedAt() != null) {
            throw new CustomException(ErrorCode.OTP_INVALID);
        }
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.OTP_EXPIRED);
        }

        String expectedHash = otpHelper.hashOtp(command.otpCode(), email);
        if (!expectedHash.equals(otp.getCodeHash())) {
            throw new CustomException(ErrorCode.OTP_INVALID);
        }

        OtpVerification used = otp.toBuilder().usedAt(LocalDateTime.now()).build();
        otpRepository.save(used);

        User updated = user.toBuilder()
                .password(passwordHasher.hash(command.newPassword()))
                .build();
        userRepository.save(updated);

        refreshTokenStore.revokeAllByUserId(user.getId());
    }
}
