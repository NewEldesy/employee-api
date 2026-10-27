
package com.liceli.employees.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank(message = "Le nouveau mot de passe est obligatoire.")
    @Size(min = 12, max = 72,
            message = "Le mot de passe doit contenir entre 12 et 72 caractères.")
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
