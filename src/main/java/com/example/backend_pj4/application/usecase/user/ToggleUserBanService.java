package com.example.backend_pj4.application.usecase.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.user.ToggleUserBanUseCase;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class ToggleUserBanService implements ToggleUserBanUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public ToggleUserBanService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    @Transactional
    public void execute(String userId, boolean ban) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (Boolean.TRUE.equals(user.getIsBanned()) == ban) {
            return;
        }

        User updated = user.toBuilder().isBanned(ban).build();
        userRepository.save(updated);

        if (ban) {
            refreshTokenRepository.revokeAllByUserId(userId);
        }
    }
}
