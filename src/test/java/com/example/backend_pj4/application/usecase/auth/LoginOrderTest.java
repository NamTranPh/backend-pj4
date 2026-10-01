package com.example.backend_pj4.application.usecase.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.application.command.auth.LoginCommand;
import com.example.backend_pj4.application.port.out.PasswordHasher;
import com.example.backend_pj4.application.usecase.admin.AdminLoginService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.config.properties.JwtProperties;
import com.example.backend_pj4.infrastructure.config.properties.LoginLockProperties;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

/**
 * Khoá hành vi của thứ tự kiểm tra khi đăng nhập:
 * mọi thất bại TRƯỚC khi mật khẩu đúng phải trả cùng một mã BAD_CREDENTIALS,
 * còn khoá tài khoản phải chặn TRƯỚC cả khi mật khẩu đúng.
 */
class LoginOrderTest {

    private static final String EMAIL = "nam@gmail.com";
    private static final String RIGHT_PASSWORD = "dung-mat-khau";
    private static final String WRONG_PASSWORD = "sai-mat-khau";

    private UserRepository userRepository;
    private PasswordHasher passwordHasher;
    private RefreshTokenRepository refreshTokenRepository;
    private LoginFailureRecorder failureRecorder;
    private LoginService loginService;
    private AdminLoginService adminLoginService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordHasher = mock(PasswordHasher.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);

        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-test-secret-test-secret-test-secret-0123456789");
        jwtProperties.setExpiration(900_000L);
        jwtProperties.setRefreshExpiration(2_592_000_000L);
        jwtProperties.setIssuer("http://localhost:3004");

        LoginLockProperties lockProperties = new LoginLockProperties();
        lockProperties.setMaxFailures(6);
        lockProperties.setFailureWindowMinutes(10);
        lockProperties.setLockMinutes(10);

        lenient().when(passwordHasher.hash(anyString())).thenReturn("$2a$10$dummy");
        lenient().when(passwordHasher.matches(anyString(), anyString()))
                .thenAnswer(inv -> RIGHT_PASSWORD.equals(inv.getArgument(0))
                        && !"$2a$10$dummy".equals(inv.getArgument(1)));
        lenient().when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        failureRecorder = new LoginFailureRecorder(userRepository, lockProperties);
        loginService = new LoginService(userRepository, passwordHasher, refreshTokenRepository,
                new JwtTokenProvider(jwtProperties), jwtProperties, failureRecorder);
        adminLoginService = new AdminLoginService(passwordHasher, loginService, failureRecorder);
    }

    private User.UserBuilder baseUser() {
        return User.builder()
                .id("usr_1")
                .email(EMAIL)
                .password("hash")
                .role(UserRole.USER)
                .emailVerified(true)
                .accountStatus(AccountStatus.ACTIVE)
                .isBanned(false);
    }

    private void given(User user) {
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(user));
    }

    private LoginCommand cmd(String password) {
        return new LoginCommand(EMAIL, password, "JUnit", "127.0.0.1");
    }

    private ErrorCode errorOf(Runnable action) {
        try {
            action.run();
            throw new AssertionError("mong đợi CustomException nhưng không có lỗi nào");
        } catch (CustomException e) {
            return e.getErrorCode();
        }
    }

    // --- Sai mật khẩu: mọi trạng thái tài khoản đều phải trả CÙNG một mã ---

    @Test
    void saiMatKhau_emailChuaDangKy_traBadCredentials() {
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.empty());

        assertThat(errorOf(() -> loginService.execute(cmd(WRONG_PASSWORD))))
                .isEqualTo(ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    void saiMatKhau_chuaVerifyEmail_traBadCredentials_khongPhaiEmailNotVerified() {
        given(baseUser().emailVerified(false).build());

        assertThat(errorOf(() -> loginService.execute(cmd(WRONG_PASSWORD))))
                .as("trả email_not_verified ở đây sẽ để lộ email nào đã đăng ký")
                .isEqualTo(ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    void saiMatKhau_taiKhoanBiBan_traBadCredentials() {
        given(baseUser().isBanned(true).build());

        assertThat(errorOf(() -> loginService.execute(cmd(WRONG_PASSWORD))))
                .isEqualTo(ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    void saiMatKhau_taiKhoanKhongActive_traBadCredentials() {
        given(baseUser().accountStatus(AccountStatus.INACTIVE).build());

        assertThat(errorOf(() -> loginService.execute(cmd(WRONG_PASSWORD))))
                .isEqualTo(ErrorCode.BAD_CREDENTIALS);
    }

    @Test
    void saiMatKhau_cuaAdmin_taiKhoanKhongPhaiAdmin_traBadCredentials() {
        given(baseUser().role(UserRole.USER).build());

        assertThat(errorOf(() -> adminLoginService.execute(cmd(WRONG_PASSWORD))))
                .as("trả admin_role_required ở đây sẽ để lộ tài khoản nào là admin")
                .isEqualTo(ErrorCode.BAD_CREDENTIALS);
    }

    // --- Đúng mật khẩu: giờ mới được trả mã cụ thể ---

    @Test
    void dungMatKhau_chuaVerifyEmail_traEmailNotVerified() {
        given(baseUser().emailVerified(false).build());

        assertThat(errorOf(() -> loginService.execute(cmd(RIGHT_PASSWORD))))
                .isEqualTo(ErrorCode.EMAIL_NOT_VERIFIED);
    }

    @Test
    void dungMatKhau_biBan_traAccountBanned() {
        given(baseUser().isBanned(true).build());

        assertThat(errorOf(() -> loginService.execute(cmd(RIGHT_PASSWORD))))
                .isEqualTo(ErrorCode.ACCOUNT_BANNED);
    }

    @Test
    void dungMatKhau_cuaAdmin_taiKhoanKhongPhaiAdmin_traAdminRoleRequired() {
        given(baseUser().role(UserRole.USER).build());

        assertThat(errorOf(() -> adminLoginService.execute(cmd(RIGHT_PASSWORD))))
                .isEqualTo(ErrorCode.ADMIN_ROLE_REQUIRED);
    }

    // --- Khoá tài khoản là rate limit: phải chặn TRƯỚC khi so mật khẩu ---

    @Test
    void dangBiKhoa_dungMatKhau_vanBiChan() {
        given(baseUser().lockedUntil(LocalDateTime.now().plusMinutes(5)).build());

        assertThat(errorOf(() -> loginService.execute(cmd(RIGHT_PASSWORD))))
                .isEqualTo(ErrorCode.ACCOUNT_TEMPORARILY_LOCKED);
    }

    @Test
    void dangBiKhoa_khongHeSoMatKhau() {
        given(baseUser().lockedUntil(LocalDateTime.now().plusMinutes(5)).build());

        assertThatThrownBy(() -> loginService.execute(cmd(WRONG_PASSWORD)))
                .isInstanceOf(CustomException.class);

        verify(passwordHasher, never()).matches(anyString(), anyString());
    }

    @Test
    void khoaHetHan_thiLoginLaiDuoc() {
        given(baseUser().lockedUntil(LocalDateTime.now().minusMinutes(1)).build());

        assertThat(loginService.execute(cmd(RIGHT_PASSWORD)).accessToken()).isNotBlank();
    }

    // --- Cân bằng thời gian: email không tồn tại vẫn phải chạy bcrypt ---

    @Test
    void emailKhongTonTai_vanChayBcrypt_deCanBangThoiGianPhanHoi() {
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.empty());

        assertThat(errorOf(() -> loginService.execute(cmd(WRONG_PASSWORD))))
                .isEqualTo(ErrorCode.BAD_CREDENTIALS);

        // Không chạy bcrypt ở nhánh này thì phản hồi nhanh hơn ~20 lần so với email có thật,
        // đủ để dò ra danh sách email dù mã lỗi đã giống nhau.
        verify(passwordHasher).matches(eq(WRONG_PASSWORD), anyString());
    }

    @Test
    void emailKhongTonTai_cuaAdmin_cungChayBcrypt() {
        when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.empty());

        assertThat(errorOf(() -> adminLoginService.execute(cmd(WRONG_PASSWORD))))
                .isEqualTo(ErrorCode.BAD_CREDENTIALS);

        verify(passwordHasher).matches(eq(WRONG_PASSWORD), anyString());
    }

    // --- Đăng nhập thành công sinh phiên có đủ thông tin thiết bị ---

    @Test
    void dangNhapThanhCong_luuSessionIdVaThongTinThietBi() {
        given(baseUser().build());

        loginService.execute(cmd(RIGHT_PASSWORD));

        verify(refreshTokenRepository).save(org.mockito.ArgumentMatchers.argThat(rt ->
                rt.getSessionId() != null
                        && rt.getTokenId() != null
                        && "JUnit".equals(rt.getUserAgent())
                        && "127.0.0.1".equals(rt.getIpAddress())
                        && rt.getLastUsedAt() != null));
    }
}
