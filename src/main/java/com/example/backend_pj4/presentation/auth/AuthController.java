package com.example.backend_pj4.presentation.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.command.auth.*;
import com.example.backend_pj4.application.dto.auth.AuthTokenResult;
import com.example.backend_pj4.application.dto.auth.RegisterResult;
import com.example.backend_pj4.application.port.in.auth.*;
import com.example.backend_pj4.infrastructure.security.AuthCookieService;
import com.example.backend_pj4.presentation.auth.request.*;
import com.example.backend_pj4.presentation.auth.response.LoginResponse;
import com.example.backend_pj4.presentation.auth.response.RegisterResponse;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final VerifyRegistrationUseCase verifyRegistrationUseCase;
    private final ResendRegistrationOtpUseCase resendRegistrationOtpUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final AuthCookieService authCookieService;

    public AuthController(RegisterUseCase registerUseCase,
                          VerifyRegistrationUseCase verifyRegistrationUseCase,
                          ResendRegistrationOtpUseCase resendRegistrationOtpUseCase,
                          LoginUseCase loginUseCase,
                          RefreshTokenUseCase refreshTokenUseCase,
                          LogoutUseCase logoutUseCase,
                          ForgotPasswordUseCase forgotPasswordUseCase,
                          ResetPasswordUseCase resetPasswordUseCase,
                          ChangePasswordUseCase changePasswordUseCase,
                          GetCurrentUserUseCase getCurrentUserUseCase,
                          AuthCookieService authCookieService) {
        this.registerUseCase = registerUseCase;
        this.verifyRegistrationUseCase = verifyRegistrationUseCase;
        this.resendRegistrationOtpUseCase = resendRegistrationOtpUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.forgotPasswordUseCase = forgotPasswordUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.authCookieService = authCookieService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResult result = registerUseCase.execute(
                new RegisterCommand(request.email(), request.password(), request.name()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(result.userId(), result.email(), result.otpSent()));
    }

    @PostMapping("/verify-registration")
    public ResponseEntity<Void> verifyRegistration(@Valid @RequestBody VerifyRegistrationRequest request) {
        verifyRegistrationUseCase.execute(
                new VerifyRegistrationCommand(request.email(), request.otpCode()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-registration-otp")
    public ResponseEntity<Void> resendRegistrationOtp(@Valid @RequestBody ResendRegistrationOtpRequest request) {
        resendRegistrationOtpUseCase.execute(
                new ResendRegistrationOtpCommand(request.email()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                                HttpServletResponse response) {
        AuthTokenResult result = loginUseCase.execute(
                new LoginCommand(request.email(), request.password()));
        authCookieService.addUserRefreshCookie(response, result.refreshToken());
        return ResponseEntity.ok(new LoginResponse(result.accessToken(), result.expiresIn()));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(
            @CookieValue(name = "${auth.cookie.refresh-name}", required = false) String refreshTokenCookie,
            HttpServletResponse response) {
        AuthTokenResult result = refreshTokenUseCase.execute(
                new RefreshTokenCommand(refreshTokenCookie));
        authCookieService.addUserRefreshCookie(response, result.refreshToken());
        return ResponseEntity.ok(new LoginResponse(result.accessToken(), result.expiresIn()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "${auth.cookie.refresh-name}", required = false) String refreshTokenCookie,
            HttpServletResponse response) {
        logoutUseCase.execute(new LogoutCommand(refreshTokenCookie));
        authCookieService.clearUserRefreshCookie(response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        forgotPasswordUseCase.execute(new ForgotPasswordCommand(request.email()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        resetPasswordUseCase.execute(
                new ResetPasswordCommand(request.email(), request.otpCode(), request.newPassword()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        String userId = getCurrentUserUseCase.execute(userDetails.getUsername()).id();
        changePasswordUseCase.execute(
                new ChangePasswordCommand(userId, request.oldPassword(), request.newPassword()));
        return ResponseEntity.ok().build();
    }
}
