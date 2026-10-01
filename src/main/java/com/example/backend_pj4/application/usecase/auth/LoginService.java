package com.example.backend_pj4.application.usecase.auth;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.LoginCommand;
import com.example.backend_pj4.application.dto.auth.AuthTokenResult;
import com.example.backend_pj4.application.port.in.auth.LoginUseCase;
import com.example.backend_pj4.application.port.out.PasswordHasher;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.config.properties.JwtProperties;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

@Service
public class LoginService implements LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final LoginFailureRecorder failureRecorder;

    /**
     * Hash giả dùng để cân bằng thời gian phản hồi khi email không tồn tại. Sinh một lần
     * lúc khởi tạo bean từ chuỗi ngẫu nhiên: luôn là chuỗi bcrypt hợp lệ (bcrypt gặp hash
     * sai định dạng sẽ trả false ngay mà không tính toán, làm hỏng mục đích), và không ai
     * đoán được nó khớp với mật khẩu nào.
     */
    private final String dummyHash;

    public LoginService(UserRepository userRepository,
                        PasswordHasher passwordHasher,
                        RefreshTokenRepository refreshTokenRepository,
                        JwtTokenProvider jwtTokenProvider,
                        JwtProperties jwtProperties,
                        LoginFailureRecorder failureRecorder) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
        this.failureRecorder = failureRecorder;
        this.dummyHash = passwordHasher.hash(UUID.randomUUID().toString());
    }

    /**
     * Thứ tự kiểm tra là một quyết định bảo mật, không phải ngẫu nhiên:
     * <ol>
     *   <li>Khoá tài khoản kiểm TRƯỚC mật khẩu — đây là rate limit. Nếu kiểm sau thì kẻ
     *       tấn công không bao giờ bị chặn khỏi việc <em>thử</em>, lockout thành vô dụng.</li>
     *   <li>Mọi thất bại TRƯỚC khi mật khẩu đúng đều trả cùng một mã {@code BAD_CREDENTIALS},
     *       và tốn thời gian như nhau. Khác mã lỗi hay khác thời gian đều để lộ email nào
     *       đã đăng ký.</li>
     *   <li>Chỉ sau khi caller chứng minh được quyền sở hữu tài khoản mới trả mã cụ thể.</li>
     * </ol>
     */
    @Override
    @Transactional
    public AuthTokenResult execute(LoginCommand command) {
        String email = command.email().toLowerCase().trim();

        User user = findUserOrFailUniformly(email, command.password());

        assertNotLocked(user);

        if (!passwordHasher.matches(command.password(), user.getPassword())) {
            failureRecorder.recordFailure(user);
            throw new CustomException(ErrorCode.BAD_CREDENTIALS);
        }

        // --- Từ đây caller đã chứng minh quyền sở hữu, trả mã lỗi cụ thể là an toàn ---

        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }
        if (Boolean.TRUE.equals(user.getIsBanned())) {
            throw new CustomException(ErrorCode.ACCOUNT_BANNED);
        }
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }

        failureRecorder.resetFailures(user);

        return issueTokens(user, false, command.userAgent(), command.ipAddress());
    }

    /**
     * Tìm user, và khi không có thì vẫn chạy một lần so khớp bcrypt trước khi báo lỗi.
     * <p>
     * Không có bước này, email chưa đăng ký trả lời sau ~5ms còn email đã đăng ký mất ~100ms
     * (bcrypt cố ý chậm) — chênh lệch đó đủ để dò ra danh sách email của hệ thống, kể cả khi
     * hai trường hợp trả về cùng một mã lỗi.
     * <p>
     * Public để {@code AdminLoginService} dùng lại cùng một quy tắc.
     */
    public User findUserOrFailUniformly(String email, String rawPassword) {
        Optional<User> found = userRepository.findByEmailIgnoreCase(email);
        if (found.isEmpty()) {
            passwordHasher.matches(rawPassword, dummyHash);
            throw new CustomException(ErrorCode.BAD_CREDENTIALS);
        }
        return found.get();
    }

    /** Public để {@code AdminLoginService} dùng lại cùng một quy tắc. */
    public void assertNotLocked(User user) {
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.ACCOUNT_TEMPORARILY_LOCKED);
        }
    }

    /**
     * @param admin true nếu token được phát từ cửa CMS. Quyết định claim {@code ch} của
     *              access token (kênh dùng được) và cờ admin của refresh token trong DB.
     */
    public AuthTokenResult issueTokens(User user, boolean admin, String userAgent, String ipAddress) {
        String channel = JwtTokenProvider.channelOf(admin);
        String tokenId = UUID.randomUUID().toString();
        // Phiên mới: sessionId sinh một lần ở đây rồi giữ nguyên qua mọi lần rotation
        String sessionId = UUID.randomUUID().toString();

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), channel);
        String refreshJwt = jwtTokenProvider.generateRefreshToken(user.getEmail(), tokenId, channel);

        LocalDateTime now = LocalDateTime.now();
        RefreshToken rt = RefreshToken.builder()
                .userId(user.getId())
                .tokenId(tokenId)
                .sessionId(sessionId)
                .admin(admin)
                .expiresAt(now.plusSeconds(jwtProperties.getRefreshExpiration() / 1000))
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .lastUsedAt(now)
                .build();
        refreshTokenRepository.save(rt);

        return new AuthTokenResult(accessToken, refreshJwt, jwtProperties.getExpiration());
    }
}
