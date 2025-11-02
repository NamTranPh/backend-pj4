package com.example.backend_pj4.presentation.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.request.auth.RequestLoginDto;
import com.example.backend_pj4.application.dto.request.auth.RequestRefreshTokenDto;
import com.example.backend_pj4.application.dto.request.auth.RequestRegisterDto;
import com.example.backend_pj4.application.dto.response.auth.ResponseAuthDto;
import com.example.backend_pj4.application.services.auth.ChangePasswordService;
import com.example.backend_pj4.application.services.auth.LoginService;
import com.example.backend_pj4.application.services.auth.RefreshTokenService;
import com.example.backend_pj4.application.services.auth.RegisterService;
import com.example.backend_pj4.common.base.BaseController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController extends BaseController {

    private final LoginService loginService;
    private final RegisterService registerService;
    private final RefreshTokenService refreshService;
    private final ChangePasswordService changePasswordService;

    @PostMapping("/register")
    @Operation(summary = "User registration")
    public ResponseAuthDto register(@Valid @RequestBody RequestRegisterDto request) {
        return registerService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "User login")
    public ResponseAuthDto login(@Valid @RequestBody RequestLoginDto dto) {
        return loginService.login(dto);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh token")
    public ResponseAuthDto refreshToken(@Valid @RequestBody RequestRefreshTokenDto request) {
        return refreshService.refreshToken(request.getRefreshToken());
    }

    //Đang lỗi
    // @PostMapping("/change-password")
    // @AuthRequired
    // @Operation(summary = "Change user password")
    // @SecurityRequirement(name = "bearerAuth")
    // public ApiResponseDto<Void> changePassword(
    //         @Valid @RequestBody RequestChangePasswordDto request,
    //         @AuthResultUser ResultUser user) {

    //     try {
    //         log.info("Changing password for user ID: {}", user.getId());

    //         boolean success = changePasswordService.changePassword(user.getId(), request);

    //         if (success) {
    //             return ApiResponseDto.success(null, "Password changed successfully");
    //         } else {
    //             return ApiResponseDto.error("User does not have a password set");
    //         }

    //     } catch (Exception e) {
    //         log.error("[changePassword] Error changing password for user {}", user.getId(), e);
    //         return ApiResponseDto.error(e.getMessage());
    //     }
    // }
}