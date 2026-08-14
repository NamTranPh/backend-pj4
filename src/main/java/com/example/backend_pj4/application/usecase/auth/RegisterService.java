package com.example.backend_pj4.application.usecase.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.RegisterCommand;
import com.example.backend_pj4.application.dto.auth.RegisterResult;
import com.example.backend_pj4.application.port.in.auth.RegisterUseCase;
import com.example.backend_pj4.application.port.out.PasswordHasher;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.constants.enums.OtpType;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class RegisterService implements RegisterUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final OtpHelper otpHelper;

    public RegisterService(UserRepository userRepository,
                           PasswordHasher passwordHasher,
                           OtpHelper otpHelper) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.otpHelper = otpHelper;
    }

    @Override
    @Transactional
    public RegisterResult execute(RegisterCommand command) {
        String email = command.email().toLowerCase().trim();
        var existing = userRepository.findByEmailIgnoreCase(email);

        if (existing.isPresent()) {
            User user = existing.get();
            if (user.getAccountStatus() != AccountStatus.INACTIVE || Boolean.TRUE.equals(user.getEmailVerified())) {
                throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            User updated = user.toBuilder()
                    .password(passwordHasher.hash(command.password()))
                    .name(command.name())
                    .build();
            userRepository.save(updated);
            otpHelper.issueOtp(email, OtpType.REGISTRATION);
            return new RegisterResult(updated.getId(), email, true);
        }

        User newUser = User.builder()
                .email(email)
                .password(passwordHasher.hash(command.password()))
                .name(command.name())
                .role(UserRole.USER)
                .emailVerified(false)
                .accountStatus(AccountStatus.INACTIVE)
                .build();

        User saved = userRepository.save(newUser);
        otpHelper.issueOtp(email, OtpType.REGISTRATION);
        return new RegisterResult(saved.getId(), saved.getEmail(), true);
    }
}
