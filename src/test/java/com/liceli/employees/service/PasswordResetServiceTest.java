
package com.liceli.employees.service;

import com.liceli.employees.model.User;
import com.liceli.employees.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Test
    void resetPasswordEncodesAndSavesNewPassword() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("ancien-hash");

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("NouveauMotDePasseFort123!"))
                .thenReturn("nouveau-hash");

        passwordResetService.resetPassword(
                "user",
                "NouveauMotDePasseFort123!"
        );

        assertEquals("nouveau-hash", user.getPassword());

        verify(passwordEncoder)
                .encode("NouveauMotDePasseFort123!");

        verify(userRepository).save(user);
    }

    @Test
    void resetPasswordFailsWhenUserDoesNotExist() {
        when(userRepository.findByUsername("inconnu"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> passwordResetService.resetPassword(
                        "inconnu",
                        "NouveauMotDePasseFort123!"
                )
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void resetPasswordUsesRealBcryptEncoding() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("ancien-hash");

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        PasswordResetService service = new PasswordResetService(
                userRepository,
                new BCryptPasswordEncoder()
        );

        String newPassword = "NouveauMotDePasseFort123!";

        service.resetPassword("user", newPassword);

        String storedHash = user.getPassword();

        assertEquals(false, storedHash.equals(newPassword));
        org.junit.jupiter.api.Assertions.assertTrue(
                new BCryptPasswordEncoder().matches(
                        newPassword,
                        storedHash
                )
        );

        verify(userRepository).save(user);
    }
}
