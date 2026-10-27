package com.liceli.employees;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Disponibilité de l'API")
public class HealthController {

    @GetMapping("/api/health")
    @Operation(summary = "Vérifier l'état de l'API", description = "Retourne un message si l'application est démarrée.")
    public String health() {
        return "Employee API is running";
    }
}