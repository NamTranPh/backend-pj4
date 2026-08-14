package com.example.backend_pj4.application.usecase.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.user.ToggleUserBanUseCase;
import com.example.backend_pj4.application.port.out.RefreshTokenStore;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class ToggleUserBanService implements ToggleUserBanUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenStore refreshTokenStore;

    public ToggleUserBanService(UserRepository userRepository, RefreshTokenStore refreshTokenStore) {
        this.userRepository = userRepository;
        this.refreshTokenStore = refreshTokenStore;
    }

    @Override
    @Transactional
    public void execute(String userId, boolean ban) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        AccountStatus newStatus = ban ? AccountStatus.BANNED : AccountStatus.ACTIVE;

        if (user.getAccountStatus() == newStatus) {
            return;
        }

        User updated = user.toBuilder().accountStatus(newStatus).build();
        userRepository.save(updated);

        if (ban) {
            refreshTokenStore.revokeAllByUserId(userId);
        }
    }
}
