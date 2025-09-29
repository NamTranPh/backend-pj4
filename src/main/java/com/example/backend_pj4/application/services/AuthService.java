// package com.example.backend_pj4.application.services;

// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// import com.example.backend_pj4.application.dto.response.AuthResponse;
// import com.example.backend_pj4.domain.entities.User;
// import com.example.backend_pj4.domain.repository.UserRepository;
// import com.example.backend_pj4.infrastructure.security.JwtTokenService;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class AuthService {

//     private final UserRepository userRepository;
//     private final PasswordEncoder passwordEncoder;
//     private final JwtTokenService jwtService;

//     public AuthResponse login(String phone, String password) {
//         User user = userRepository.findByPhone(phone)
//                 .orElseThrow(() -> new RuntimeException("User not found"));

//         if (!passwordEncoder.matches(password, user.getPassword())) {
//             throw new RuntimeException("Invalid credentials");
//         }

//         String token = jwtService.generateToken(
//                 new org.springframework.security.core.userdetails.User(
//                         user.getPhone(),
//                         user.getPassword(),
//                         java.util.Collections.emptyList()
//                 )
//         );

//         return AuthResponse.success(user.getPhone(), token, ""); // refreshToken tạm để trống
//     }

//     public AuthResponse register(User user) {
//         user.setPassword(passwordEncoder.encode(user.getPassword()));
//         userRepository.save(user);
//         return login(user.getPhone(), user.getPassword());
//     }
// }

// package com.example.backend_pj4.application.services;

// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// import com.example.backend_pj4.application.dto.request.RegisterRequest;
// import com.example.backend_pj4.application.dto.response.AuthResponse;
// import com.example.backend_pj4.domain.entities.User;
// import com.example.backend_pj4.domain.repository.UserRepository;
// import com.example.backend_pj4.infrastructure.security.JwtTokenService;

// import jakarta.transaction.Transactional;
// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// @Transactional
// public class AuthService {

//     private final UserRepository userRepository;
//     private final PasswordEncoder passwordEncoder;
//     private final JwtTokenService jwtService;

//     public AuthResponse login(String phone, String password) {
//         User user = userRepository.findByPhone(phone)
//                 .orElseThrow(() -> new RuntimeException("User not found"));

//         if (!passwordEncoder.matches(password, user.getPassword())) {
//             throw new RuntimeException("Invalid credentials");
//         }

//         String token = jwtService.generateToken(
//                 new org.springframework.security.core.userdetails.User(
//                         user.getPhone(),
//                         user.getPassword(),
//                         java.util.Collections.emptyList()));

//         return AuthResponse.success(user.getPhone(), token, ""); // refreshToken tạm để trống
//     }

//     // public AuthResponse register(User user) {
//     // user.setPassword(passwordEncoder.encode(user.getPassword()));
//     // userRepository.save(user);
//     // return login(user.getPhone(), user.getPassword());
//     // }

//     public AuthResponse register(RegisterRequest request) {
//         // Kiểm tra phone đã tồn tại chưa
//         if (userRepository.existsByPhone(request.getPhone())) {
//             throw new RuntimeException("Phone already exists");
//         }

//         // Tạo entity user mới
//         User user = User.builder()
//                 .phone(request.getPhone())
//                 .password(passwordEncoder.encode(request.getPassword()))
//                 .isActive(true)
//                 .build();

//         // Lưu vào DB
//         userRepository.save(user);

//         // Sinh token JWT
//         String accessToken = jwtService.generateToken(
//                 new org.springframework.security.core.userdetails.User(
//                         user.getPhone(),
//                         user.getPassword(),
//                         java.util.Collections.emptyList()));

//         // Trả về response chuẩn
//         return AuthResponse.success(user.getPhone(), accessToken, ""); // refreshToken tạm trống
//     }
// }

// // 1

package com.example.backend_pj4.application.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.request.LoginRequest;
import com.example.backend_pj4.application.dto.request.RegisterRequest;
import com.example.backend_pj4.application.dto.response.AuthResponse;
import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.domain.enums.MembershipStatus;
import com.example.backend_pj4.infrastructure.databases.entities.RoleEntity;
import com.example.backend_pj4.infrastructure.databases.entities.UserEntity;
import com.example.backend_pj4.infrastructure.databases.repository.JpaRoleRepository;
import com.example.backend_pj4.infrastructure.databases.repository.JpaUserRepository;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final JpaUserRepository userRepository; // Na ná hằng số
    private final JpaRoleRepository roleRepository; // Na ná hằng số
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhone(),
                        request.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        // check user exists
        userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return AuthResponse.success(request.getPhone(), accessToken, refreshToken);
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already exists");
        }

        RoleEntity userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("User role not found"));

        UserEntity user = new UserEntity();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(userRole);
        user.setMembershipStatus(MembershipStatus.FREE);
        user.setIsActive(true);

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getPhone());
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        return AuthResponse.success(user.getPhone(), accessToken, refreshToken);
    }

    // public AuthResponse refreshToken(String refreshToken) {
    // String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
    // UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    // if (!jwtTokenProvider.validateToken(refreshToken, userDetails)) {
    // throw new RuntimeException("Invalid refresh token");
    // }

    // String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
    // String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

    // userRepository.findByPhone(username)
    // .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // return AuthResponse.success(username, newAccessToken, newRefreshToken);
    // }
}
