package com.lms.security;

import com.lms.data.models.Role;
import com.lms.data.models.User;
import com.lms.data.respositories.UserRepository;
import com.lms.utils.Env;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuperAdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        String email = Env.getOrDefault("SUPERADMIN_EMAIL", "superadmin@lms.com");
        String password = Env.getOrDefault("SUPERADMIN_PASSWORD", "supersecurepassword");

        System.out.println("Loaded SuperAdmin email: " + email);

        if (userRepository.findByEmailIgnoreCase(email).isEmpty()) {
            User superAdmin = User.builder()
                    .email(email)
                    .name("Default SuperAdmin")
                    .password(passwordEncoder.encode(password))
                    .role(Role.SUPER_ADMIN)
                    .build();

            userRepository.save(superAdmin);
            System.out.println("SuperAdmin created: " + email);
        } else {
            System.out.println("SuperAdmin already exists. Skipping creation.");
        }
    }

}
