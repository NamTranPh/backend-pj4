package com.example.backend_pj4.application.usecase.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.LoginCommand;
import com.example.backend_pj4.application.dto.auth.AuthTokenResult;
import com.example.backend_pj4.application.port.in.auth.LoginUseCase;
import com.example.backend_pj4.application.port.out.LoginLockManager;
import com.example.backend_pj4.application.port.out.PasswordHasher;
import com.example.backend_pj4.application.port.out.RefreshTokenStore;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.config.properties.JwtProperties;
import com.example.backend_pj4.infrastructure.security.CustomUserDetailsService;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

@Service
public class LoginService implements LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final LoginLockManager loginLockManager;
    private final RefreshTokenStore refreshTokenStore;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final JwtProperties jwtProperties;

    public LoginService(UserRepository userRepository,
                        PasswordHasher passwordHasher,
                        LoginLockManager loginLockManager,
                        RefreshTokenStore refreshTokenStore,
                        JwtTokenProvider jwtTokenProvider,
                        CustomUserDetailsService userDetailsService,
                        JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.loginLockManager = loginLockManager;
        this.refreshTokenStore = refreshTokenStore;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.jwtProperties = jwtProperties;
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

        // DB fallback lockout check
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.ACCOUNT_TEMPORARILY_LOCKED);
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

        if (!passwordHasher.matches(command.password(), user.getPassword())) {
            loginLockManager.recordFailure(email);
            recordFailureToDb(user);
            throw new CustomException(ErrorCode.BAD_CREDENTIALS);
        }

        loginLockManager.resetFailures(email);
        resetFailuresOnDb(user);

        return issueTokens(user, false);
    }

    public AuthTokenResult issueTokens(User user, boolean admin) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String tokenId = UUID.randomUUID().toString();
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshJwt = jwtTokenProvider.generateRefreshToken(userDetails, tokenId);

        RefreshToken rt = RefreshToken.builder()
                .userId(user.getId())
                .tokenId(tokenId)
                .admin(admin)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtProperties.getRefreshExpiration() / 1000))
                .build();
        refreshTokenStore.save(rt);

        return new AuthTokenResult(accessToken, refreshJwt, jwtProperties.getExpiration());
    }

    private void recordFailureToDb(User user) {
        LocalDateTime now = LocalDateTime.now();
        int attempts = user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() + 1 : 1;
        LocalDateTime firstFailure = user.getFirstFailureAt() != null ? user.getFirstFailureAt() : now;

        User updated = user.toBuilder()
                .failedLoginAttempts(attempts)
                .firstFailureAt(firstFailure)
                .build();
        userRepository.save(updated);
    }

    private void resetFailuresOnDb(User user) {
        if (user.getFailedLoginAttempts() != null && user.getFailedLoginAttempts() > 0) {
            User reset = user.toBuilder()
                    .failedLoginAttempts(0)
                    .firstFailureAt(null)
                    .lockedUntil(null)
                    .build();
            userRepository.save(reset);
        }
    }
}
