
package com.liceli.employees.controller;

import com.liceli.employees.config.SecurityConfig;
import com.liceli.employees.security.CustomUserDetailsService;
import com.liceli.employees.service.PasswordResetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.liceli.employees.security.JwtAuthFilter;
import com.liceli.employees.security.JwtUtil;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.doThrow;

@WebMvcTest(value = PasswordResetController.class, excludeAutoConfiguration = {UserDetailsServiceAutoConfiguration.class})
@AutoConfigureMockMvc
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtUtil.class})
class PasswordResetSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PasswordResetService passwordResetService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private static final String URL =
            "/api/admin/users/user/reset-password";

    private static final String VALID_BODY =
            """
            {
                "newPassword": "NouveauMotDePasseFort123!"
            }
            """;

    @Test
    void anonymousUserCannotResetPassword() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(passwordResetService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void regularUserCannotResetPassword() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isForbidden());

        verifyNoInteractions(passwordResetService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanRequestPasswordReset() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNoContent());

        verify(passwordResetService).resetPassword(
                "user",
                "NouveauMotDePasseFort123!"
        );
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCannotResetPasswordWithBlankPassword() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"newPassword": ""}
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(passwordResetService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCannotResetPasswordWithTooShortPassword() throws Exception {
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"newPassword": "abc123"}
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(passwordResetService);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCannotResetPasswordForUnknownUser() throws Exception {
        doThrow(new ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND,
                "Utilisateur introuvable."
        )).when(passwordResetService).resetPassword(
                "inconnu",
                "NouveauMotDePasseFort123!"
        );

        mockMvc.perform(put(
                        "/api/admin/users/inconnu/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNotFound());
    }
}
