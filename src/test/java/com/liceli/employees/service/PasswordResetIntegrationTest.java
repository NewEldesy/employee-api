
package com.liceli.employees.service;

import com.liceli.employees.model.User;
import com.liceli.employees.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({
        PasswordResetService.class,
        PasswordResetIntegrationTest.TestConfig.class
})
class PasswordResetIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @Test
    void resetPasswordPersistsBcryptHashInDatabase() {
        User user = new User();
        user.setUsername("integration-user");
        user.setPassword(passwordEncoder.encode("AncienMotDePasse123!"));
        user.setEnabled(true);

        userRepository.saveAndFlush(user);

        String newPassword = "NouveauMotDePasseFort123!";

        passwordResetService.resetPassword(
                "integration-user",
                newPassword
        );

        entityManager.flush();
        entityManager.clear();

        User updatedUser = userRepository
                .findByUsername("integration-user")
                .orElseThrow();

        assertNotEquals(newPassword, updatedUser.getPassword());
        assertTrue(passwordEncoder.matches(
                newPassword,
                updatedUser.getPassword()
        ));
        assertFalse(passwordEncoder.matches(
                "AncienMotDePasse123!",
                updatedUser.getPassword()
        ));
        assertTrue(updatedUser.isEnabled());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}
