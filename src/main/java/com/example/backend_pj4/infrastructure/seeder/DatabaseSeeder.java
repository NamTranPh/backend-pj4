package com.example.backend_pj4.infrastructure.seeder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.common.enums.RoleEnum;
import com.example.backend_pj4.infrastructure.databases.entities.RoleEntity;
import com.example.backend_pj4.infrastructure.databases.entities.UserEntity;
import com.example.backend_pj4.infrastructure.databases.repository.JpaRoleRepository;
import com.example.backend_pj4.infrastructure.databases.repository.JpaUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final JpaRoleRepository roleRepository;
    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Cho phép override qua application.properties hoặc env vars (ví dụ:
    // APP_ADMIN_PHONE, APP_ADMIN_PASSWORD
    @Value("${app.seed.admin.phone:admin}")
    private String adminPhone;

    @Value("${app.seed.admin.password:admin}")
    private String adminPassword;

    @Value("${app.seed.admin.email:admin@local}")
    private String adminEmail;

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedAdminUser();
    }

    private void seedRoles() {
        if (!roleRepository.existsByRoleName("ADMIN")) {
            RoleEntity adminRole = new RoleEntity();
            adminRole.setRoleName("ADMIN");
            roleRepository.save(adminRole);
        }

        if (!roleRepository.existsByRoleName("STAFF")) {
            RoleEntity staffRole = new RoleEntity();
            staffRole.setRoleName("STAFF");
            roleRepository.save(staffRole);
        }

        if (!roleRepository.existsByRoleName("USER")) {
            RoleEntity userRole = new RoleEntity();
            userRole.setRoleName("USER");
            roleRepository.save(userRole);
        }
    }

    private void seedAdminUser() {
        if (!userRepository.existsByPhone(adminPhone)) {
            RoleEntity adminRole = roleRepository.findByRoleName(RoleEnum.ADMIN).orElseThrow();

            UserEntity admin = new UserEntity();
            admin.setEmail(adminEmail);
            admin.setPhone(adminPhone);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setName("Administrator");
            admin.setRole(adminRole);
            admin.setIsActive(true);

            userRepository.save(admin);
            log.info("Created admin user: phone={} / password={}", adminPhone, adminPassword);
        } else {
            log.info("Admin user already exists with phone={}", adminPhone);
        }
    }
}

// package com.example.backend_pj4.infrastructure.seeder;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;

// import com.example.backend_pj4.infrastructure.databases.entities.RoleEntity;
// import com.example.backend_pj4.infrastructure.databases.entities.UserEntity;
// import
// com.example.backend_pj4.infrastructure.databases.repository.JpaRoleRepository;
// import
// com.example.backend_pj4.infrastructure.databases.repository.JpaUserRepository;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Component
// @RequiredArgsConstructor
// @Slf4j
// public class DatabaseSeeder implements CommandLineRunner {

// private final JpaRoleRepository roleRepository;
// private final JpaUserRepository userRepository;
// private final PasswordEncoder passwordEncoder;

// // Admin mặc định
// private final String adminPhone = "1234567890";
// private final String adminPassword = "123456";
// private final String adminEmail = "admin@local";

// @Override
// public void run(String... args) throws Exception {
// seedRoles();
// seedAdminUser();
// }

// private void seedRoles() {
// if (!roleRepository.existsByRoleName("ADMIN")) {
// RoleEntity adminRole = new RoleEntity();
// adminRole.setRoleName("ADMIN");
// roleRepository.save(adminRole);
// log.info("Created ADMIN role");
// }

// if (!roleRepository.existsByRoleName("USER")) {
// RoleEntity userRole = new RoleEntity();
// userRole.setRoleName("USER");
// roleRepository.save(userRole);
// log.info("Created USER role");
// }
// }

// private void seedAdminUser() {
// // Kiểm tra theo phone vì bạn chỉ đăng ký bằng phone
// if (!userRepository.existsByPhone(adminPhone)) {
// RoleEntity adminRole = roleRepository.findByRoleName("ADMIN").orElseThrow();

// UserEntity admin = new UserEntity();
// admin.setEmail(adminEmail);
// admin.setPhone(adminPhone);
// admin.setPassword(passwordEncoder.encode(adminPassword)); // BCrypt
// admin.setName("Administrator");
// admin.setRole(adminRole);
// admin.setIsActive(true);

// userRepository.save(admin);
// log.info("Created admin user: phone={} / password={}", adminPhone,
// adminPassword);
// } else {
// log.info("Admin user already exists with phone={}", adminPhone);
// }
// }
// }
