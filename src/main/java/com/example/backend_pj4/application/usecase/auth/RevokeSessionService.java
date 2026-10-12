package com.example.backend_pj4.application.usecase.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.auth.RevokeSessionUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RevokeSessionService implements RevokeSessionUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    public RevokeSessionService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    @Transactional
    public void execute(String userId, String sessionId) {
        // Chỉ cho thu hồi phiên thuộc về chính người gọi, nếu không thì biết sessionId của người
        // khác là đăng xuất được họ. Phiên không tồn tại và phiên của người khác trả cùng một lỗi
        // để không tiết lộ sessionId nào có thật.
        if (!refreshTokenRepository.existsActiveSession(userId, sessionId)) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        refreshTokenRepository.revokeAllBySessionId(sessionId);
        log.info("Đã đăng xuất phiên sessionId={} của userId={}", sessionId, userId);
    }
}
