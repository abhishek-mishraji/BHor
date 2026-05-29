package com.handsofretail.hor.config;

import com.handsofretail.hor.entity.AdminUser;
import com.handsofretail.hor.enums.UserRole;
import com.handsofretail.hor.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String adminEmail = "vivek@hor.com";

        boolean adminExists = adminUserRepository.existsByEmail(adminEmail);

        if (!adminExists) {

            AdminUser admin = AdminUser.builder()
                    .fullName("Vivek")
                    .email(adminEmail)
                    .passwordHash(
                            passwordEncoder.encode("Vivek@1"))
                    .role(UserRole.ADMIN)
                    .build();

            adminUserRepository.save(admin);

            // System.out.println("Default admin created");
        }
    }
}