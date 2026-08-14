package com.example.backend_pj4.infrastructure.database.seeders;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;
import com.example.backend_pj4.infrastructure.database.repositories.UserJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@admin.vn";

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user already exists with email={}", adminEmail);
            return;
        }

        UserJpaEntity admin = new UserJpaEntity();
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setName("Administrator");
        admin.setRole(UserRole.ADMIN);
        admin.setEmailVerified(true);
        admin.setAccountStatus(AccountStatus.ACTIVE);

        userRepository.save(admin);
        log.info("Created admin user: email={} (password hardcoded in seeder)", adminEmail);
    }
}

