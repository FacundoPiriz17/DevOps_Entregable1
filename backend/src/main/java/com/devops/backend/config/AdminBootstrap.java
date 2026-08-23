package com.devops.backend.config;

import com.devops.backend.user.Role;
import com.devops.backend.user.User;
import com.devops.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminBootstrap(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${admin.bootstrap.email:admin@devops.local}") String adminEmail,
                           @Value("${admin.bootstrap.password:ChangeMe123!}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }
        userRepository.save(new User("Admin", adminEmail, passwordEncoder.encode(adminPassword), Role.ADMIN));
    }
}
