package com.example.backend_pj4.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.request.LoginRequest;
import com.example.backend_pj4.application.dto.request.RegisterRequest;
import com.example.backend_pj4.application.dto.response.AuthResponse;
import com.example.backend_pj4.application.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register new user account")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
    
    // @PostMapping("/refresh")
    // @Operation(summary = "Refresh token", description = "Get new access token using refresh token")
    // public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
    //     String refreshToken = request.get("refreshToken");
    //     AuthResponse response = authService.refreshToken(refreshToken);
    //     return ResponseEntity.ok(response);
    // }
}

// package com.example.backend_pj4.presentation.controllers;

// import java.util.Map;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.backend_pj4.application.dto.request.LoginRequest;
// import com.example.backend_pj4.application.dto.request.RegisterRequest;
// import com.example.backend_pj4.application.dto.response.AuthResponse;
// import com.example.backend_pj4.application.services.AuthService;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/api/auth")
// @RequiredArgsConstructor
// @Tag(name = "Authentication", description = "Authentication management APIs")
// public class AuthController {

//     private final AuthService authService;

//     @PostMapping("/login")
//     public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
//         AuthResponse response = authService.login(
//                 request.getPhone(),
//                 request.getPassword());
//         return ResponseEntity.ok(response);
//     }

//     @PostMapping("/register")
//     @Operation(summary = "User registration", description = "Register new user account")
//     public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
//         AuthResponse response = authService.register(request);
//         return ResponseEntity.ok(response);
//     }

//     // @PostMapping("/refresh")
//     // @Operation(summary = "Refresh token", description = "Get new access token
//     // using refresh token")
//     // public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String,
//     // String> request) {
//     // String refreshToken = request.get("refreshToken");
//     // AuthResponse response = authService.refreshToken(refreshToken);
//     // return ResponseEntity.ok(response);
//     // }
// }
