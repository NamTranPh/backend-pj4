//Chú ý vì đăng nhập bằng phone nên phone đang để không được null và không giống
//(gmail cũng không được giống) nên nếu chưa chinh được thì xóa dữ liệu đi không sửa ở application

package com.example.backend_pj4.infrastructure.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.entities.Role;
import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.repositories.RoleRepository;
import com.example.backend_pj4.domain.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedAdminUser();
    }

    private void seedRoles() {
        if (!roleRepository.existsByRoleName("ADMIN")) {
            Role adminRole = new Role();
            adminRole.setRoleName("ADMIN");
            roleRepository.save(adminRole);
            log.info("Created ADMIN role");
        }

        if (!roleRepository.existsByRoleName("USER")) {
            Role userRole = new Role();
            userRole.setRoleName("USER");
            roleRepository.save(userRole);
            log.info("Created USER role");
        }
    }

    private void seedAdminUser() {
        if (!userRepository.existsByEmail("admin@movieapi.com")) {
            Role adminRole = roleRepository.findByRoleName("ADMIN").orElseThrow();

            User admin = new User();
            admin.setEmail("namtran@gmail.com");
            admin.setPhone("0899467682");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("Administrator");
            admin.setRole(adminRole);
            admin.setIsActive(true);

            userRepository.save(admin);
            log.info("Created admin user: admin@movieapi.com / admin123");
        }
    }
}
