package com.example.backend_pj4.infrastructure.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;
import com.example.backend_pj4.infrastructure.database.repositories.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserJpaEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        boolean active = user.getAccountStatus() == AccountStatus.ACTIVE;
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(!active)
                .credentialsExpired(false)
                .disabled(!active)
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(UserJpaEntity user) {
        // Trả về role dạng "ROLE_ADMIN" hoặc "ROLE_USER"
        if (user.getRole() != null) {
            return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );
        }
        // Nếu user chưa có role, trả về ROLE_USER mặc định
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
}

