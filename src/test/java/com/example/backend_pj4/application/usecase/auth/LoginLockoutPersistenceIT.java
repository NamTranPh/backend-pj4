package com.example.backend_pj4.application.usecase.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.backend_pj4.application.command.auth.LoginCommand;
import com.example.backend_pj4.application.port.out.PasswordHasher;
import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.config.properties.LoginLockProperties;

/**
 * Test chạm DB THẬT, không mock repository.
 *
 * <p><b>Vì sao cần:</b> {@code LoginOrderTest} mock {@code userRepository.save()} nên chỉ xác
 * minh method <em>được gọi</em>. Nó không thấy được rằng bản ghi đó bị transaction rollback
 * cuốn đi khi {@code CustomException} ném ra — test xanh trong khi tính năng khoá tài khoản
 * chết hoàn toàn. Chỉ có test đọc lại dữ liệu từ DB sau khi exception ném ra mới bắt được.
 *
 * <p>Không dùng {@code @Transactional} ở lớp test: cần transaction thật sự commit/rollback
 * thì mới kiểm chứng được {@code REQUIRES_NEW}.
 *
 * <p><b>Cần Docker.</b> {@code disabledWithoutDocker = true} để build không đỏ trên máy chưa
 * bật Docker — nhưng như vậy test này KHÔNG chạy, và fix chưa được kiểm chứng. Bật Docker
 * Desktop rồi chạy lại:
 * <pre>./mvnw.cmd test -Dtest=LoginLockoutPersistenceIT</pre>
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class LoginLockoutPersistenceIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @Container
    @ServiceConnection(name = "redis")
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    private static final String RIGHT_PASSWORD = "dung-mat-khau";
    private static final String WRONG_PASSWORD = "sai-mat-khau";

    @Autowired
    private LoginService loginService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordHasher passwordHasher;
    @Autowired
    private LoginLockProperties lockProperties;

    private String email;

    @BeforeEach
    void createUser() {
        email = "u" + UUID.randomUUID().toString().substring(0, 8) + "@test.vn";
        userRepository.save(User.builder()
                .email(email)
                .password(passwordHasher.hash(RIGHT_PASSWORD))
                .name("Test User")
                .role(UserRole.USER)
                .emailVerified(true)
                .accountStatus(AccountStatus.ACTIVE)
                .isBanned(false)
                .build());
    }

    private void loginExpectingFailure(String password) {
        assertThatThrownBy(() -> loginService.execute(
                new LoginCommand(email, password, "JUnit", "127.0.0.1")))
                .isInstanceOf(CustomException.class);
    }

    private User reload() {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow();
    }

    @Test
    void saiMatKhau_soLanThatBaiPhaiDuocGHIVAODB_khongBiRollbackCuonDi() {
        loginExpectingFailure(WRONG_PASSWORD);

        assertThat(reload().getFailedLoginAttempts())
                .as("bản ghi đếm phải sống sót qua rollback của transaction đăng nhập")
                .isEqualTo(1);
        assertThat(reload().getFirstFailureAt()).isNotNull();
    }

    @Test
    void saiMatKhauNhieuLan_boDemTangDan() {
        loginExpectingFailure(WRONG_PASSWORD);
        loginExpectingFailure(WRONG_PASSWORD);
        loginExpectingFailure(WRONG_PASSWORD);

        assertThat(reload().getFailedLoginAttempts()).isEqualTo(3);
    }

    @Test
    void saiDuNguong_taiKhoanBiKHOA_vaMatKhauDungCungKhongVaoDuoc() {
        for (int i = 0; i < lockProperties.getMaxFailures(); i++) {
            loginExpectingFailure(WRONG_PASSWORD);
        }

        assertThat(reload().getLockedUntil())
                .as("locked_until phải thực sự được ghi khi vượt ngưỡng")
                .isNotNull();

        // Đúng mật khẩu vẫn bị chặn — đây là điểm khiến lockout có tác dụng
        loginExpectingFailure(RIGHT_PASSWORD);
    }

    @Test
    void dangNhapDung_xoaBoDemThatBai() {
        loginExpectingFailure(WRONG_PASSWORD);
        assertThat(reload().getFailedLoginAttempts()).isEqualTo(1);

        loginService.execute(new LoginCommand(email, RIGHT_PASSWORD, "JUnit", "127.0.0.1"));

        assertThat(reload().getFailedLoginAttempts()).isZero();
        assertThat(reload().getFirstFailureAt()).isNull();
    }
}
