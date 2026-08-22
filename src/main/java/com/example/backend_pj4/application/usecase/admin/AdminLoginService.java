package com.example.backend_pj4.application.usecase.admin;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.LoginCommand;
import com.example.backend_pj4.application.dto.auth.AuthTokenResult;
import com.example.backend_pj4.application.port.in.admin.AdminLoginUseCase;
import com.example.backend_pj4.application.port.out.LoginLockManager;
import com.example.backend_pj4.application.port.out.PasswordHasher;
import com.example.backend_pj4.application.port.out.RefreshTokenStore;
import com.example.backend_pj4.application.usecase.auth.LoginService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.config.properties.JwtProperties;
import com.example.backend_pj4.infrastructure.security.CustomUserDetailsService;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

@Service
public class AdminLoginService implements AdminLoginUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final LoginLockManager loginLockManager;
    private final LoginService loginService;

    public AdminLoginService(UserRepository userRepository,
                              PasswordHasher passwordHasher,
                              LoginLockManager loginLockManager,
                              LoginService loginService) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.loginLockManager = loginLockManager;
        this.loginService = loginService;
    }

    @Override
    @Transactional
    public AuthTokenResult execute(LoginCommand command) {
        String email = command.email().toLowerCase().trim();

        if (loginLockManager.isLocked(email)) {
            throw new CustomException(ErrorCode.ACCOUNT_TEMPORARILY_LOCKED);
        }

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS));

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.ACCOUNT_TEMPORARILY_LOCKED);
        }

        if (user.getRole() != UserRole.ADMIN) {
            throw new CustomException(ErrorCode.ADMIN_ROLE_REQUIRED);
        }

        if (Boolean.TRUE.equals(user.getIsBanned())) {
            throw new CustomException(ErrorCode.ACCOUNT_BANNED);
        }
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }

        if (!passwordHasher.matches(command.password(), user.getPassword())) {
            loginLockManager.recordFailure(email);
            throw new CustomException(ErrorCode.BAD_CREDENTIALS);
        }

        loginLockManager.resetFailures(email);
        return loginService.issueTokens(user, true);
    }
}
