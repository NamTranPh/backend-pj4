package com.example.backend_pj4.application.usecase.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.ChangePasswordCommand;
import com.example.backend_pj4.application.port.in.auth.ChangePasswordUseCase;
import com.example.backend_pj4.application.port.out.PasswordHasher;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordPolicy passwordPolicy;

    public ChangePasswordService(UserRepository userRepository,
                                  PasswordHasher passwordHasher,
                                  RefreshTokenRepository refreshTokenRepository,
                                  PasswordPolicy passwordPolicy) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordPolicy = passwordPolicy;
    }

    @Override
    @Transactional
    public void execute(ChangePasswordCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordHasher.matches(command.oldPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.OLD_PASSWORD_INCORRECT);
        }

        // Ngưỡng độ dài phụ thuộc role nên phải kiểm ở đây, không kiểm được bằng @Size trên DTO
        passwordPolicy.assertValid(user, command.newPassword());

        if (passwordHasher.matches(command.newPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.NEW_PASSWORD_SAME_AS_OLD);
        }

        // Tăng tokenVersion để access token cũ chết ngay, không chờ hết 15 phút
        User updated = user.toBuilder()
                .password(passwordHasher.hash(command.newPassword()))
                .tokenVersion(LoginService.tokenVersionOf(user) + 1)
                .build();
        userRepository.save(updated);

        refreshTokenRepository.revokeAllByUserId(user.getId());
    }
}
