package com.liceli.employees.controller;

import com.liceli.employees.dto.LoginRequest;
import com.liceli.employees.dto.LoginResponse;
import com.liceli.employees.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentification et obtention du token JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
        summary = "Connexion",
        description = "Authentifie un utilisateur et retourne un token JWT à inclure dans les requêtes suivantes via `Authorization: Bearer <token>`.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Identifiants de connexion",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginRequest.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Connexion admin",
                    value = """
                        {"username": "admin", "password": "ChangeMe123!"}
                        """
                )
            )
        )
    )
    @ApiResponse(responseCode = "200", description = "Token JWT généré",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = LoginResponse.class)))
    @ApiResponse(responseCode = "400", description = "Champs manquants ou invalides")
    @ApiResponse(responseCode = "401", description = "Identifiants incorrects ou compte désactivé")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return ResponseEntity.ok(new LoginResponse(jwtUtil.generateToken(userDetails)));

        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Compte désactivé.");
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides.");
        }
    }
}
