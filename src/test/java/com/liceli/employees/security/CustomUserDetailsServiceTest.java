
package com.liceli.employees.security;

import com.liceli.employees.model.Role;
import com.liceli.employees.model.User;
import com.liceli.employees.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void disabledUserIsReturnedAsDisabled() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("bcrypt-hash");
        user.setEnabled(false);
        user.setRoles(Set.of(new Role("ROLE_USER")));

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("user");

        assertFalse(userDetails.isEnabled());
        assertEquals("user", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void unknownUserThrowsUsernameNotFoundException() {
        when(userRepository.findByUsername("inconnu"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService
                        .loadUserByUsername("inconnu")
        );
    }
}
