package com.example.backend_pj4.application.services.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.request.auth.RequestChangePasswordDto;
import com.example.backend_pj4.common.constants.ErrorCodes;
import com.example.backend_pj4.common.utils.CustomException;
import com.example.backend_pj4.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean changePassword(String userId, RequestChangePasswordDto dto) {

        String oldPassword = dto.getOldPassword();
        String newPassword = dto.getNewPassword();

        // ==== Validate input ====
        if (oldPassword == null || oldPassword.isBlank()) {
            log.error(ErrorCodes.OLD_PASSWORD_REQUIRED.getMessage());
            throw new CustomException(ErrorCodes.OLD_PASSWORD_REQUIRED);
        }

        if (newPassword == null || newPassword.isBlank()) {
            log.error(ErrorCodes.NEW_PASSWORD_REQUIRED.getMessage());
            throw new CustomException(ErrorCodes.NEW_PASSWORD_REQUIRED);
        }

        // ==== Find user ====
        var existingUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error(ErrorCodes.USER_NOT_FOUND.getMessage());
                    return new CustomException(ErrorCodes.USER_NOT_FOUND);
                });

        // ==== If user has no password (maybe OAuth) ====
        if (existingUser.getPassword() == null) {
            log.warn("User {} does not have a password set", userId);
            return false;
        }

        // ==== Check old password ====
        if (!passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
            log.error(ErrorCodes.OLD_PASSWORD_INCORRECT.getMessage());
            throw new CustomException(ErrorCodes.OLD_PASSWORD_INCORRECT);
        }

        // ==== Encode and update new password ====
        String hashedNewPassword = passwordEncoder.encode(newPassword);
        existingUser.setPassword(hashedNewPassword);
        userRepository.save(existingUser);

        log.info("Password changed successfully for user {}", userId);
        return true;
    }
}
