// package com.example.backend_pj4.infrastructure.security;


// import java.util.Collection;
// import java.util.Collections;

// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import com.example.backend_pj4.domain.repository.UserRepository;
// import com.example.backend_pj4.infrastructure.databases.entities.UserEntity;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class CustomUserDetailsService implements UserDetailsService {
    
//     private final UserRepository userRepository;
    
//     @Override
//     public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
//         UserEntity user = userRepository.findByPhone(phone)(phone)
//                 .orElseThrow(() -> new UsernameNotFoundException("User not found: " + phone));
        
//         return org.springframework.security.core.userdetails.User.builder()
//                 .username(user.getEmail())
//                 .password(user.getPassword())
//                 .authorities(getAuthorities(user))
//                 .accountExpired(false)
//                 .accountLocked(!user.getIsActive())
//                 .credentialsExpired(false)
//                 .disabled(!user.getIsActive())
//                 .build();
//     }
    
//     private Collection<? extends GrantedAuthority> getAuthorities(UserEntity user) {
//         return Collections.singletonList(
//             new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName())
//         );
//     }
// }


package com.example.backend_pj4.infrastructure.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.backend_pj4.infrastructure.databases.entities.UserEntity;
import com.example.backend_pj4.infrastructure.databases.repository.JpaUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final JpaUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        // Sửa cú pháp: xóa (phone) thừa
        UserEntity user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + phone));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getPhone()) // dùng phone làm username
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(!user.getIsActive())
                .credentialsExpired(false)
                .disabled(!user.getIsActive())
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(UserEntity user) {
        // Trả về role dạng "ROLE_ADMIN" hoặc "ROLE_USER"
        if (user.getRole() != null && user.getRole().getRoleName() != null) {
            return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName())
            );
        }
        // Nếu user chưa có role, trả về ROLE_USER mặc định
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
