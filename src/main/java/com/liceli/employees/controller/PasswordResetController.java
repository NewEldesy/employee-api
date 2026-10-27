
package com.liceli.employees.controller;

import com.liceli.employees.dto.ResetPasswordRequest;
import com.liceli.employees.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(
            PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PutMapping("/{username}/reset-password")
    public ResponseEntity<Void> resetPassword(
            @PathVariable String username,
            @Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(
                username,
                request.getNewPassword()
        );

        return ResponseEntity.noContent().build();
    }
}
