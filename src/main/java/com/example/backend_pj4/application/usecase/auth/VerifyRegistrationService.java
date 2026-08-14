package com.example.backend_pj4.application.usecase.auth;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.VerifyRegistrationCommand;
import com.example.backend_pj4.application.port.in.auth.VerifyRegistrationUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.constants.enums.OtpType;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.OtpVerification;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.OtpVerificationRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class VerifyRegistrationService implements VerifyRegistrationUseCase {

    private final UserRepository userRepository;
    private final OtpVerificationRepository otpRepository;
    private final OtpHelper otpHelper;

    public VerifyRegistrationService(UserRepository userRepository,
                                      OtpVerificationRepository otpRepository,
                                      OtpHelper otpHelper) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.otpHelper = otpHelper;
    }

    @Override
    @Transactional
    public void execute(VerifyRegistrationCommand command) {
        String email = command.email().toLowerCase().trim();

        OtpVerification otp = otpRepository.findByEmailAndType(email, OtpType.REGISTRATION)
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

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User verified = user.toBuilder()
                .emailVerified(true)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        userRepository.save(verified);
    }
}
