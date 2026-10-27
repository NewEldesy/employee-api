
package com.liceli.employees.config;

import com.liceli.employees.model.Role;
import com.liceli.employees.model.User;
import com.liceli.employees.repository.RoleRepository;
import com.liceli.employees.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap.enabled:false}") boolean bootstrapEnabled,
            @Value("${APP_BOOTSTRAP_USER_PASSWORD:}") String userPassword,
            @Value("${APP_BOOTSTRAP_ADMIN_PASSWORD:}") String adminPassword) {

        return args -> {
            if (!bootstrapEnabled) {
                return;
            }

            if (userPassword.isBlank() || adminPassword.isBlank()) {
                throw new IllegalStateException(
                        "Les mots de passe de bootstrap doivent être configurés."
                );
            }

            createUserIfNotExists(
                    "user",
                    userPassword,
                    "ROLE_USER",
                    userRepository,
                    roleRepository,
                    passwordEncoder
            );

            createUserIfNotExists(
                    "admin",
                    adminPassword,
                    "ROLE_ADMIN",
                    userRepository,
                    roleRepository,
                    passwordEncoder
            );
        };
    }

    private void createUserIfNotExists(
            String username,
            String rawPassword,
            String roleName,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        if (userRepository.existsByUsername(username)) {
            return;
        }

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEnabled(true);
        user.setRoles(Set.of(role));

        userRepository.save(user);
    }
}
