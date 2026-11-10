package com.example.bank.config;

import com.example.bank.model.AppUser;
import com.example.bank.model.Role;
import com.example.bank.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

@Configuration
public class DataInitializer {
    @Value("${app.admin.username:}")
    private String adminUsername;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Bean
    public ApplicationRunner seedDefaultAdmin(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (!StringUtils.hasText(adminUsername) || !StringUtils.hasText(adminPassword)) {
                return;
            }

            AppUser admin = appUserRepository.findByUsername(adminUsername)
                    .orElseGet(() -> {
                        AppUser newAdmin = new AppUser();
                        newAdmin.setUsername(adminUsername);
                        return newAdmin;
                    });

            if (admin.getPassword() == null || !passwordEncoder.matches(adminPassword, admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode(adminPassword));
            }
            admin.setRole(Role.ADMIN);

            appUserRepository.save(admin);
        };
    }
}
