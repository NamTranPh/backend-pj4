package com.example.backend_pj4.application.usecase.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.ChangePasswordCommand;
import com.example.backend_pj4.application.port.in.auth.ChangePasswordUseCase;
import com.example.backend_pj4.application.port.out.PasswordHasher;
import com.example.backend_pj4.application.port.out.RefreshTokenStore;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final RefreshTokenStore refreshTokenStore;

    public ChangePasswordService(UserRepository userRepository,
                                  PasswordHasher passwordHasher,
                                  RefreshTokenStore refreshTokenStore) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.refreshTokenStore = refreshTokenStore;
    }

    @Override
    @Transactional
    public void execute(ChangePasswordCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordHasher.matches(command.oldPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.OLD_PASSWORD_INCORRECT);
        }

        if (passwordHasher.matches(command.newPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.NEW_PASSWORD_SAME_AS_OLD);
        }

        User updated = user.toBuilder()
                .password(passwordHasher.hash(command.newPassword()))
                .build();
        userRepository.save(updated);

        refreshTokenStore.revokeAllByUserId(user.getId());
    }
}
