package com.example.backend_pj4.application.usecase.auth;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.auth.AuthTokenResult;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.config.properties.JwtProperties;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * Cơ chế xoay vòng refresh token, dùng chung cho cả hai kênh.
 * <p>
 * Trước đây {@code RefreshTokenService} và {@code AdminRefreshTokenService} là hai bản sao
 * của nhau. Bản admin được thêm check {@code isAdmin} còn bản user thì không — đó chính là
 * lỗ hổng cho phép cửa user phát ra vé admin. Gộp lại và đưa kênh thành tham số khiến hai
 * chiều không thể lệch nhau nữa.
 * <p>
 * Ranh giới giữa hai kênh vẫn giữ nguyên ở tầng trên: hai input port, hai use case,
 * hai controller, hai cookie riêng.
 */
@Slf4j
@Service
public class RefreshTokenRotator {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    public RefreshTokenRotator(JwtTokenProvider jwtTokenProvider,
                               RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository,
                               JwtProperties jwtProperties) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtProperties = jwtProperties;
    }

    /**
     * @param expectedChannel kênh mà endpoint gọi tới thuộc về. Token của kênh khác bị từ
     *                        chối theo cả hai chiều: cửa user không nhận token admin, cửa
     *                        admin không nhận token user.
     */
    public AuthTokenResult rotate(String rawRefreshToken,
                                  String expectedChannel,
                                  String userAgent,
                                  String ipAddress) {
        RefreshToken stored = loadValidToken(rawRefreshToken, expectedChannel);

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // Không phát refresh token 30 ngày mới cho tài khoản đã bị vô hiệu hoá
        if (Boolean.TRUE.equals(user.getIsBanned()) || user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_REVOKED);
        }

        refreshTokenRepository.revokeByTokenId(stored.getTokenId());

        String newTokenId = UUID.randomUUID().toString();
        String newAccessToken = jwtTokenProvider.generateAccessToken(
                user.getEmail(), expectedChannel, LoginService.tokenVersionOf(user));
        String newRefreshJwt = jwtTokenProvider.generateRefreshToken(user.getEmail(), newTokenId, expectedChannel);

        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.save(RefreshToken.builder()
                .userId(user.getId())
                .tokenId(newTokenId)
                // sessionId giữ nguyên: rotation không mở phiên mới, vẫn là cùng một thiết bị
                .sessionId(stored.getSessionId())
                .admin(stored.isAdmin())
                .expiresAt(now.plusSeconds(jwtProperties.getRefreshExpiration() / 1000))
                .userAgent(userAgent != null ? userAgent : stored.getUserAgent())
                .ipAddress(ipAddress != null ? ipAddress : stored.getIpAddress())
                .lastUsedAt(now)
                .build());

        return new AuthTokenResult(newAccessToken, newRefreshJwt, jwtProperties.getExpiration());
    }

    /**
     * Verify chữ ký, loại token, hạn dùng, trạng thái thu hồi và kênh.
     * Dùng chung với {@link RefreshTokenRevoker} để logout áp cùng một bộ luật.
     */
    RefreshToken loadValidToken(String rawRefreshToken, String expectedChannel) {
        String tokenId;
        try {
            String tokenType = jwtTokenProvider.getTokenTypeFromToken(rawRefreshToken);
            tokenId = jwtTokenProvider.getTokenIdFromToken(rawRefreshToken);
            if (!"refresh".equals(tokenType) || tokenId == null) {
                tokenId = null;
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        if (tokenId == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }
        if (jwtTokenProvider.isTokenExpired(rawRefreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        RefreshToken stored = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_REVOKED));

        if (!JwtTokenProvider.channelOf(stored.isAdmin()).equals(expectedChannel)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_REVOKED);
        }

        if (stored.getRevokedAt() != null) {
            handleReuse(stored);
            throw new CustomException(ErrorCode.REFRESH_TOKEN_REVOKED);
        }

        return stored;
    }

    /**
     * Xử lý việc dùng lại một refresh token đã bị thu hồi.
     *
     * <p>Client bình thường không bao giờ gửi lại token nó đã đổi, nên đây gần như chắc chắn là
     * dấu hiệu token bị đánh cắp: kẻ tấn công dùng token trước, nạn nhân dùng sau (hoặc ngược
     * lại) và một trong hai chạm vào token đã revoke. Nếu bỏ qua, kẻ tấn công cứ xoay vòng token
     * mới vô thời hạn trong khi nạn nhân chỉ thấy "bị đăng xuất" rồi đăng nhập lại như thường.
     *
     * <p>Phản ứng: thu hồi toàn bộ token của <b>phiên đó</b> ({@code sessionId}), không phải toàn
     * bộ phiên của người dùng. Nhờ {@code sessionId} có từ đợt trước, nạn nhân chỉ mất đúng thiết
     * bị bị ảnh hưởng thay vì bị đá khỏi mọi thiết bị.
     *
     * <p><b>Cửa sổ ân hạn:</b> hai tab mở cùng lúc có thể cùng gọi refresh với một token — tab thứ
     * hai đọc cookie trước khi tab đầu kịp ghi đè. Đó là chuyện bình thường, không phải bị trộm.
     * Token vừa revoke trong {@code reuse-grace-seconds} giây được coi là trường hợp này: vẫn từ
     * chối request, nhưng không thu hồi cả phiên.
     */
    private void handleReuse(RefreshToken stored) {
        if (stored.getSessionId() == null) {
            return;
        }

        long secondsSinceRevoke = Duration.between(stored.getRevokedAt(), LocalDateTime.now()).getSeconds();
        if (secondsSinceRevoke <= jwtProperties.getReuseGraceSeconds()) {
            log.debug("Refresh token dùng lại trong cửa sổ ân hạn ({}s), coi là refresh song song",
                    secondsSinceRevoke);
            return;
        }

        log.warn("Phát hiện dùng lại refresh token đã thu hồi ({}s trước) — thu hồi toàn bộ phiên sessionId={}",
                secondsSinceRevoke, stored.getSessionId());
        refreshTokenRepository.revokeAllBySessionId(stored.getSessionId());
    }
}
