package com.example.backend_pj4.application.query.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.auth.SessionResult;
import com.example.backend_pj4.application.port.in.auth.ListSessionsQuery;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;
import com.example.backend_pj4.domain.repository.filter.SessionFilter;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * Liệt kê phiên đăng nhập.
 *
 * <p>Dữ liệu đã được ghi sẵn từ lúc đăng nhập: mỗi phiên có {@code sessionId} cố định, kèm
 * {@code user_agent}, {@code ip_address}, {@code last_used_at}. Rotation giữ nguyên
 * {@code sessionId} nên nhiều dòng token cùng thuộc một phiên; chỉ dòng chưa thu hồi và chưa
 * hết hạn mới được coi là phiên đang hoạt động.
 */
@Slf4j
@Service
public class ListSessionsQueryHandler implements ListSessionsQuery {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ListSessionsQueryHandler(RefreshTokenRepository refreshTokenRepository,
                                    JwtTokenProvider jwtTokenProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<SessionResult> execute(SessionFilter filter, String currentRefreshToken,
                                             PageQuery query) {
        String currentSessionId = resolveCurrentSessionId(currentRefreshToken);

        return refreshTokenRepository.find(filter, query)
                .map(token -> toResult(token, currentSessionId));
    }

    /** Phiên của chính request hiện tại, suy từ refresh token trong cookie. */
    private String resolveCurrentSessionId(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            return null;
        }
        try {
            String tokenId = jwtTokenProvider.getTokenIdFromToken(rawRefreshToken);
            return tokenId == null ? null : refreshTokenRepository.findByTokenId(tokenId)
                    .map(RefreshToken::getSessionId)
                    .orElse(null);
        } catch (Exception e) {
            // Cookie hỏng hoặc hết hạn thì chỉ mất nhãn thiết bị này, không ảnh hưởng danh sách
            log.debug("Không xác định được phiên hiện tại: {}", e.getMessage());
            return null;
        }
    }

    private SessionResult toResult(RefreshToken token, String currentSessionId) {
        return new SessionResult(
                token.getSessionId(),
                token.getUserAgent(),
                token.getIpAddress(),
                token.isAdmin(),
                token.getSessionId() != null && token.getSessionId().equals(currentSessionId),
                token.getLastUsedAt(),
                token.getCreatedAt(),
                token.getExpiresAt());
    }
}
