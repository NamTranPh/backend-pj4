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
        if (!roleRepository.existsByRoleName(RoleEnum.ADMIN)) {
            RoleEntity adminRole = new RoleEntity();
            adminRole.setRoleName(RoleEnum.ADMIN);
            roleRepository.save(adminRole);
        }

        if (!roleRepository.existsByRoleName(RoleEnum.STAFF)) {
            RoleEntity staffRole = new RoleEntity();
            staffRole.setRoleName(RoleEnum.STAFF);
            roleRepository.save(staffRole);
        }

        if (!roleRepository.existsByRoleName(RoleEnum.USER)) {
            RoleEntity userRole = new RoleEntity();
            userRole.setRoleName(RoleEnum.USER);
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
