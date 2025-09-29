// //File seeder tạo dl mẫu
// package com.example.backend_pj4.infrastructure.seeder;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;

// import com.example.backend_pj4.domain.repository.RoleRepository;
// import com.example.backend_pj4.domain.repository.UserRepository;
// import com.example.backend_pj4.infrastructure.databases.entities.RoleEntity;
// import com.example.backend_pj4.infrastructure.databases.entities.UserEntity;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Component
// @RequiredArgsConstructor
// @Slf4j
// public class DatabaseSeeder implements CommandLineRunner {

//     private final RoleRepository roleRepository;
//     private final UserRepository userRepository;
//     private final PasswordEncoder passwordEncoder;

//     @Override
//     public void run(String... args) throws Exception {
//         seedRoles();
//         seedAdminUser();
//     }

//     private void seedRoles() {
//         if (!roleRepository.existsByRoleName("ADMIN")) {
//             RoleEntity adminRole = new RoleEntity();
//             adminRole.setRoleName("ADMIN");
//             roleRepository.save(adminRole);
//             log.info("Created ADMIN role");
//         }

//         if (!roleRepository.existsByRoleName("USER")) {
//             RoleEntity userRole = new RoleEntity();
//             userRole.setRoleName("USER");
//             roleRepository.save(userRole);
//             log.info("Created USER role");
//         }
//     }

//     private void seedAdminUser() {
//         if (!userRepository.existsByEmail("admin@gmail.com")) {
//             RoleEntity adminRole = roleRepository.findByRoleName("ADMIN").orElseThrow();

//             UserEntity admin = new UserEntity();
//             admin.setEmail("admin@gmail.com");
//             admin.setPhone("0899467682");
//             admin.setPassword(passwordEncoder.encode("admin123"));
//             admin.setName("Administrator");
//             admin.setRole(adminRole);
//             admin.setIsActive(true);

//             userRepository.save(admin);
//             log.info("Created admin user: admin@gmail.com / admin123");
//         }
//     }
// }
